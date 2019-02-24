#include <events/mbed_events.h>

#include <mbed.h>
#include "ble/BLE.h"
#include "ble/Gap.h"
#include "ButtonService.h"
#include "ButtonServiceSecond.h"
#include "ReadSensorService.h"
#include "TOFSensor.h"

#include "mbed.h"
#include "vl53l0x_api.h"
#include "vl53l0x_platform.h"
#include "vl53l0x_i2c_platform.h"

#define USE_I2C_2V8
#define BTN_1 17
#define LED_1 21
#define LED_2 22
#define PIR_1 15
#define PIR_2 16
#define READ_SENSOR 14

InterruptIn pir_1(P0_15);
InterruptIn pir_2(P0_16);
InterruptIn read_sensor(P0_14);

Serial pc(USBTX, USBRX);
DigitalOut led(LED3);

static EventQueue eventQueue(/* event count */ 10 * EVENTS_EVENT_SIZE);

const static char     DEVICE_NAME[] = "A legend 27";
static const uint16_t uuid16_list[] = {ButtonService::BUTTON_SERVICE_UUID, ButtonServiceSecond::BUTTON_SERVICE_UUID, ReadSensorService::READ_SERVICE_UUID};



ButtonService *buttonServicePtr;
ButtonServiceSecond *buttonServicePtr2;
ReadSensorService *readSensorPtr;
TOFSensor *tofSensorPtr;

VL53L0X_Dev_t MyDevice;
VL53L0X_Dev_t *pMyDevice;
VL53L0X_RangingMeasurementData_t   *pRangingMeasurementData;

VL53L0X_Error WaitMeasurementDataReady(VL53L0X_DEV Dev) {
    VL53L0X_Error Status = VL53L0X_ERROR_NONE;
    uint8_t NewDatReady=0;
    uint32_t LoopNb;
    
    if (Status == VL53L0X_ERROR_NONE) {
        LoopNb = 0;
        do {
            Status = VL53L0X_GetMeasurementDataReady(Dev, &NewDatReady);
            if ((NewDatReady == 0x01) || Status != VL53L0X_ERROR_NONE) {
                break;
            }
            LoopNb = LoopNb + 1;
            VL53L0X_PollingDelay(Dev);
        } while (LoopNb < VL53L0X_DEFAULT_MAX_LOOP);

        if (LoopNb >= VL53L0X_DEFAULT_MAX_LOOP) {
            Status = VL53L0X_ERROR_TIME_OUT;
        }
    }

    return Status;
}

VL53L0X_Error WaitStopCompleted(VL53L0X_DEV Dev) {
    VL53L0X_Error Status = VL53L0X_ERROR_NONE;
    uint32_t StopCompleted=0;
    uint32_t LoopNb;

    if (Status == VL53L0X_ERROR_NONE) {
        LoopNb = 0;
        do {
            Status = VL53L0X_GetStopCompletedStatus(Dev, &StopCompleted);
            if ((StopCompleted == 0x00) || Status != VL53L0X_ERROR_NONE) {
                break;
            }
            LoopNb = LoopNb + 1;
            VL53L0X_PollingDelay(Dev);
        } while (LoopNb < VL53L0X_DEFAULT_MAX_LOOP);

        if (LoopNb >= VL53L0X_DEFAULT_MAX_LOOP) {
            Status = VL53L0X_ERROR_TIME_OUT;
        }

    }

    return Status;
}

void init_TOF(void) {
    int x=1, measure=0;
    int ave=0, sum=0;
    VL53L0X_Error Status = VL53L0X_ERROR_NONE;
    pMyDevice = &MyDevice;
    VL53L0X_RangingMeasurementData_t    RangingMeasurementData;
    pRangingMeasurementData    = &RangingMeasurementData;
    VL53L0X_Version_t                   Version;
    
    // Initialize Comms
    pMyDevice->I2cDevAddr      = 0x52;
    pMyDevice->comms_type      =  1;
    pMyDevice->comms_speed_khz =  400;
    
    
    VL53L0X_ERROR_CONTROL_INTERFACE;
    VL53L0X_RdWord(&MyDevice, VL53L0X_REG_OSC_CALIBRATE_VAL,0);
    VL53L0X_DataInit(&MyDevice); // Data initialization
    Status = VL53L0X_ERROR_NONE;
    uint32_t refSpadCount;
    uint8_t isApertureSpads;
    uint8_t VhvSettings;
    uint8_t PhaseCal;
    
    VL53L0X_StaticInit(pMyDevice); // Device Initialization
    VL53L0X_PerformRefSpadManagement(pMyDevice, &refSpadCount, &isApertureSpads); // Device Initialization
    VL53L0X_PerformRefCalibration(pMyDevice, &VhvSettings, &PhaseCal); // Device Initialization
    VL53L0X_SetDeviceMode(pMyDevice, VL53L0X_DEVICEMODE_CONTINUOUS_RANGING); // Setup in single ranging mode
    VL53L0X_SetLimitCheckValue(pMyDevice, VL53L0X_CHECKENABLE_SIGNAL_RATE_FINAL_RANGE, (FixPoint1616_t)(0.25*65536)); //High Accuracy mode, see API PDF
    VL53L0X_SetLimitCheckValue(pMyDevice, VL53L0X_CHECKENABLE_SIGMA_FINAL_RANGE, (FixPoint1616_t)(18*65536)); //High Accuracy mode, see API PDF
    VL53L0X_SetMeasurementTimingBudgetMicroSeconds(pMyDevice, 200000); //High Accuracy mode, see API PDF
    VL53L0X_StartMeasurement(pMyDevice);
}    

int getTOFMeasurement(void) {
        int measure = 0;
        WaitMeasurementDataReady(pMyDevice);
        VL53L0X_GetRangingMeasurementData(pMyDevice, pRangingMeasurementData);
        measure=pRangingMeasurementData->RangeMilliMeter;
        //printf("In loop measurement %d\n", mea);
       
        // Clear the interrupt
        VL53L0X_ClearInterruptMask(pMyDevice, VL53L0X_REG_SYSTEM_INTERRUPT_GPIO_NEW_SAMPLE_READY);
        VL53L0X_PollingDelay(pMyDevice);
        
        printf("Measure is is %d\n", measure);
        return measure; 
}    

void init_PIRS(void) {
    NRF_GPIO->PIN_CNF[BTN_1] = 0x0C;
    NRF_GPIO->PIN_CNF[PIR_2] = GPIO_PIN_CNF_PULL_Pullup; 
    NRF_GPIO->PIN_CNF[PIR_1] = GPIO_PIN_CNF_PULL_Pullup;    
    NRF_GPIO->PIN_CNF[READ_SENSOR] = GPIO_PIN_CNF_PULL_Pullup;
}

void init_leds(void) {   
    NRF_GPIO->DIRSET |= (0x1 << (LED_1));
    NRF_GPIO->DIRSET |= (0x1 << (LED_2));
    NRF_GPIO->DIRSET |= (0x1 << (BTN_1));
}

// Set the output pin 21 to low (LEDs are active low)
void led1_on(void) {
    NRF_GPIO->OUTCLR &= (0x1 << (LED_1));    
}


// Set the output pin 22 to low 
void led2_on(void) {
    NRF_GPIO->OUTCLR &= (0x1 << (LED_2));
}

void led_off(void) {
    NRF_GPIO->OUTSET |= (0x1 << (LED_1));
    NRF_GPIO->OUTSET |= (0x1 << (LED_2));
}





//Callbacks
void buttonPressedCallback(void)
{
    eventQueue.call(Callback<void(bool)>(buttonServicePtr, &ButtonService::updateButtonState), true);
}
void buttonReleasedCallback(void)
{
    eventQueue.call(Callback<void(bool)>(buttonServicePtr, &ButtonService::updateButtonState), false);
}

void buttonPressedCallback2(void) 
{
    eventQueue.call(Callback<void(bool)>(buttonServicePtr2, &ButtonServiceSecond::updateButtonState), true);    
}

void buttonReleasedCallback2(void) 
{
    eventQueue.call(Callback<void(bool)>(buttonServicePtr2, &ButtonServiceSecond::updateButtonState), false);    
}

void readSensorTriggeredCallback(void) 
{
    eventQueue.call(Callback<void(bool)>(readSensorPtr, &ReadSensorService::updateButtonState), true);    
}
void readSensorReleasedCallback(void) 
{
    eventQueue.call(Callback<void(bool)>(readSensorPtr, &ReadSensorService::updateButtonState), false);    
}

void tofSensorDistanceChanged(void) 
{
    int measurement = getTOFMeasurement();
    printf("Measurement in tofSensor BLE function is %d\n", measurement);
    eventQueue.call(Callback<void(uint16_t)>(tofSensorPtr, &TOFSensor::updateDistance), (uint16_t) measurement);   
}    

void disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params)
{
    BLE::Instance().gap().startAdvertising(); // restart advertising
}



void onBleInitError(BLE &ble, ble_error_t error)
{
    /* Initialization error handling should go here */
}

void bleInitComplete(BLE::InitializationCompleteCallbackContext *params)
{
    BLE&        ble   = params->ble;
    ble_error_t error = params->error;

    if (error != BLE_ERROR_NONE) {
        /* In case of error, forward the error handling to onBleInitError */
        onBleInitError(ble, error);
        return;
    }

    /* Ensure that it is the default instance of BLE */
    if(ble.getInstanceID() != BLE::DEFAULT_INSTANCE) {
        return;
    }


    ble.gap().onDisconnection(disconnectionCallback);

    pir_1.fall(buttonPressedCallback);
    pir_1.rise(buttonReleasedCallback);
    read_sensor.fall(readSensorTriggeredCallback);
    read_sensor.rise(readSensorReleasedCallback);
    
    pir_2.fall(buttonPressedCallback2);
    pir_2.rise(buttonReleasedCallback2);

    /* Setup primary service. */
    buttonServicePtr = new ButtonService(ble, false );
    buttonServicePtr2 = new ButtonServiceSecond(ble, false);
    readSensorPtr = new ReadSensorService(ble, false);
    tofSensorPtr = new TOFSensor(ble);
    
    /* setup advertising */
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE);
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t *)uuid16_list, sizeof(uuid16_list));
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME));
    ble.gap().setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
    ble.gap().setAdvertisingInterval(1000); /* 1000ms. */
    ble.gap().startAdvertising();
   
}
void scheduleBleEventsProcessing(BLE::OnEventsToProcessCallbackContext* context) {
    BLE &ble = BLE::Instance();
    eventQueue.call(Callback<void()>(&ble, &BLE::processEvents));
    eventQueue.call_every(1000 , tofSensorDistanceChanged);
}


int main()
{
    
    init_TOF();
    init_PIRS();
    init_leds();


    
    BLE &ble = BLE::Instance();
    ble.onEventsToProcess(scheduleBleEventsProcessing);
    ble.init(bleInitComplete);
    

    
    eventQueue.dispatch_forever();
    
     led_off(); 
   
    
    while(1){
        //Iterate over the LED's whilst the PIR is active 
       // ble.waitForEvent();
         //If pir_1 1 is pressed turn on LED 1
        if(!(NRF_GPIO->IN >> PIR_1 & 0x1)) {
            led1_on();
            
            
            
        }
        // If PIR 2 then turn on LED 1
        if(!(NRF_GPIO->IN >> PIR_2 & 0x1)) {
            led2_on();
        }
        
      
          
        // Turn off the LEDs
        led_off();     
    }

    return 0;
}
