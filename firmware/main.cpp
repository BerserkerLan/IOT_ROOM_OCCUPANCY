#include "mbed.h"
#include "time.h"
#include "VL53L0X.h"
#include "ble/BLE.h"
#include "PeopleInService.h"
#include "PeopleOutService.h"
#include "ReadSensorService.h"

#define range1_addr (0x56)
#define range2_addr (0x60)
#define range1_XSHUT   p12
#define range2_XSHUT   p13
#define VL53L0_I2C_SDA   p30
#define VL53L0_I2C_SCL   p7  
#define READ_SENSOR 14

//Commented out the prints due to how slow they make the board

DigitalOut led(LED1, 1);
InterruptIn read_sensor(P0_14);


//UUIDs for different services and characteristics
uint16_t customServiceUUID  = 0xB000;
uint16_t readCharUUID       = 0xB001;
uint16_t readChar2UUID      = 0xB002;
uint16_t readChar3UUID      = 0xB060;
uint16_t distance1UUID      = 0xB004; 

int indexForTimeStamp1 = 0;
int indexForTimeStamp2 = 0;

int orderForTriggered = 1;


int bleConnected = 0;


static PeopleInService *peopleInServicePtr;
static PeopleOutService *peopleOutServicePtr;

const static char     DEVICE_NAME[]        = "A legend 27"; // change this
static const uint16_t uuid16_list[]        = {0xFFFF}; //Custom UUID, FFFF is reserved for development

/* Set Up custom Characteristics */
//Value of 1 if distance sensor is triggered, otherwise 0
static bool readValue[1] = {0};
ReadOnlyArrayGattCharacteristic<bool, sizeof(readValue)> readChar(readCharUUID, readValue);
//Same for the other distance sensor
static bool readValue2[1] = {0};
ReadOnlyArrayGattCharacteristic<bool, sizeof(readValue2)> readChar2(readChar2UUID, readValue2);

//Stores the timestamps of one of the distance sensors
static uint8_t timeStamp1[40] = {0};
ReadOnlyArrayGattCharacteristic<uint8_t, sizeof(timeStamp1)> timeChar1(readChar2UUID, timeStamp1, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);

//Characteristic for first time stamp
GattCharacteristic *characteristics1[] = {&timeChar1};
GattService        customService1(customServiceUUID, characteristics1, sizeof(characteristics1) / sizeof(GattCharacteristic *));

//Stores the timestamps of the other distance sensor
static uint8_t timeStamp2[40] = {0};
ReadOnlyArrayGattCharacteristic<uint8_t, sizeof(timeStamp2)> timeChar2(readChar3UUID, timeStamp2, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);

//Characteristic for second one, two seperate for more size
GattCharacteristic *characteristics2[] = {&timeChar2};
GattService        customService2(distance1UUID, characteristics2, sizeof(characteristics2) / sizeof(GattCharacteristic *));


uint16_t seconds = 0;

uint32_t distance1;
uint32_t distance2;
bool doorOpen;

int timeStamp1Index = 0;
int timeStamp2Index = 0;


void connectionCallback(const Gap::ConnectionCallbackParams_t *)
{
    bleConnected = 1;
}

void disconnectionCallback(const Gap::DisconnectionCallbackParams_t *)
{
    bleConnected = 0;
     BLE::Instance(BLE::DEFAULT_INSTANCE).gap().startAdvertising();
}


//// This was a function for debugging, commenting it out since not used anymore
//void writeValueOfDistance(uint8_t params[]) 
//{
//    //printf("Distance1 update : %d\n\r", params[0]);
//    BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(readChar.getValueHandle(), params, sizeof(params));
//}


//Updates the characteristics of the person out state to show one of the distance sensor is triggered
void setPersonOutState(int state) {
            uint8_t params[] = {state};
            BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(readChar2.getValueHandle(), params, sizeof(params));
            if (doorOpen) {
                peopleOutServicePtr->updateButtonState(state);
            }
             else {
                peopleInServicePtr->updateButtonState(0);  
            }
}
//Updates the characteristics of the person in state to show one of the distance sensor is triggered
void setPersonInState(int state) {
            uint8_t params[] = {state};
            BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(readChar2.getValueHandle(), params, sizeof(params));
            if (doorOpen) {
                peopleInServicePtr->updateButtonState(state);
            }
            else {
                peopleInServicePtr->updateButtonState(0);  
            }
}


void readSensorTriggeredCallback(void) 
{
    doorOpen = true;   
}
void readSensorReleasedCallback(void) 
{
    doorOpen = false;
}

void bleInitComplete(BLE::InitializationCompleteCallbackContext *params)
{
    BLE &ble          = params->ble;
    ble_error_t error = params->error;
    
    if (error != BLE_ERROR_NONE) {
        return;
    }

    ble.gap().onDisconnection(disconnectionCallback);
    ble.gap().onConnection(connectionCallback);
    //ble.gattServer().onDataWritten(writeCharCallback);

    /* Setup advertising */
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE); // BLE only, no classic BT
    ble.gap().setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED); // advertising type
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME)); // add name
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t *)uuid16_list, sizeof(uuid16_list)); // UUID's broadcast in advertising packet
    ble.gap().setAdvertisingInterval(1000); // 100ms.


    read_sensor.fall(readSensorTriggeredCallback);
    read_sensor.rise(readSensorReleasedCallback);

    peopleInServicePtr = new PeopleInService(ble, false);
    peopleOutServicePtr = new PeopleOutService(ble, false);
    /* Add our timestamp services */
    ble.addService(customService1);
    ble.addService(customService2);

    /* Start advertising */
    ble.gap().startAdvertising();
}

uint8_t params1[30] = {0};
void updateTimestamp1() 
{
       if (!doorOpen) {
            return;  
        }
       if (indexForTimeStamp1 >= 30) {
            indexForTimeStamp1 = 0;  
        }

        params1[indexForTimeStamp1] = orderForTriggered;
        orderForTriggered++;
        indexForTimeStamp1++;
        
        seconds++;
       
       BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(timeChar1.getValueHandle(), params1, sizeof(params1));
}

uint8_t params2[30] = {0};
void updateTimestamp2() 
{
       if (!doorOpen) {
            return;  
        }
       if (indexForTimeStamp2 >= 30) {
            indexForTimeStamp2 = 0;  
        }

       params2[indexForTimeStamp2] = orderForTriggered;
       orderForTriggered++;
        indexForTimeStamp2++;
        
        seconds++;
       
       BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(timeChar2.getValueHandle(), params2, sizeof(params2));
}
//Set read sensor as Pullup
void init_read(void) {
     NRF_GPIO->PIN_CNF[READ_SENSOR] = GPIO_PIN_CNF_PULL_Pullup;   
}


int main(void)
{
    //printf("Doing main ");
    init_read();
    static DevI2C devI2c(VL53L0_I2C_SDA,VL53L0_I2C_SCL); 
    static DigitalOut shutdown1_pin(range1_XSHUT);
    static VL53L0X range1(&devI2c, &shutdown1_pin, NC);
    static DigitalOut shutdown2_pin(range2_XSHUT);
    static VL53L0X range2(&devI2c, &shutdown2_pin, NC);
    
    
        

    int iteration = 0;
    
    //Indicate that the door is open at the start before we start the while loop
    if (!(NRF_GPIO->IN >> READ_SENSOR & 0x1)) {
           doorOpen = 1;
    }
    
     range1.init_sensor(range1_addr);
    range2.init_sensor(range2_addr);

    /* initialize stuff */
    //printf("\n\r********* Starting Main Loop *********\n\r");
    
    
    
    BLE& ble = BLE::Instance(BLE::DEFAULT_INSTANCE);
    ble.init(bleInitComplete);
    /* SpinWait for initialization to complete. This is necessary because the
     * BLE object is used in the main loop below. */
    while (ble.hasInitialized()  == false) { /* spin loop */ }
    

    /*Get datas*/
    int status1;
    int status2;
    /* Infinite loop waiting for BLE interrupt events */
    while (true) 
    {
       // printf("Door open : %i\r\n", doorOpen);
        iteration++;
        //printf("Connection state : %i\r\n", bleConnected);
        status1 = range1.get_distance(&distance1);  //Lets say less than 300 is person detected
        if (status1 == VL53L0X_ERROR_NONE) {
            //printf("Range1 [mm]:            %6ld\r\n", distance1);
            
            if (distance1 <= 700 && distance1 != 0 && doorOpen) 
            {
               // printf("Gonna update that sweeeet IN STATE");
               if (!bleConnected) 
               {
                     updateTimestamp1();
               }
               BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(timeChar1.getValueHandle(), params1, sizeof(params1));
                setPersonInState(true);
                
            }
            else {
                setPersonInState(false);
            }
        } else {
            setPersonInState(false);
          //  printf("Range1 [mm]:                --\r\n");
        }

        status2 = range2.get_distance(&distance2);
        if (status2 == VL53L0X_ERROR_NONE) {
           // printf("Range2 [mm]:            %6ld\r\n", distance2);
            
            if (distance2 <= 700 && distance2 != 0 && doorOpen) 
            {
              //  printf("Gonna update that sweeeet OUT STATE");
              if (!bleConnected) 
               {
                     updateTimestamp2();
               }
               BLE::Instance(BLE::DEFAULT_INSTANCE).gattServer().write(timeChar2.getValueHandle(), params2, sizeof(params2));
                setPersonOutState(true);
            }
            else {
                setPersonOutState(false);    
            }
        
        } else {
            setPersonOutState(false);
           // printf("Range2 [mm]:                --\r\n");
        
        }
        if (iteration == 3) //Loose second counter, every 2.7 loops is a second, so it is not extremely accurate........
        {
            seconds++;
            iteration = 0;    
        }
             
    }
}