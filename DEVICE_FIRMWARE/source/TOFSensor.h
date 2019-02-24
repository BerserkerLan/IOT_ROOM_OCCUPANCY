#ifndef __BLE_ENVIRONMENTAL_SERVICE_H__
#define __BLE_ENVIRONMENTAL_SERVICE_H__

#include "ble/BLE.h"

//TODO : REFACTOR
class TOFSensor {
public:
    typedef int16_t  TemperatureType_t;
    typedef uint16_t HumidityType_t;
    typedef uint32_t PressureType_t;

    /**
     * @brief   TOFSensor constructor.
     * @param   ble Reference to BLE device.
     * @param   temperature_en Enable this characteristic.
     * @param   humidity_en Enable this characteristic.
     * @param   pressure_en Enable this characteristic.
     */
    TOFSensor(BLE& _ble) :
        ble(_ble),
        temperatureCharacteristic(GattCharacteristic::UUID_TEMPERATURE_CHAR, &temperature),
        humidityCharacteristic(GattCharacteristic::UUID_HUMIDITY_CHAR, &humidity),
        pressureCharacteristic(GattCharacteristic::UUID_PRESSURE_CHAR, &pressure)
    {
        static bool serviceAdded = false; /* We should only ever need to add the information service once. */
        if (serviceAdded) {
            return;
        }

        GattCharacteristic *charTable[] = { &humidityCharacteristic,
                                            &pressureCharacteristic,
                                            &temperatureCharacteristic };

        GattService environmentalService(GattService::UUID_ENVIRONMENTAL_SERVICE, charTable, sizeof(charTable) / sizeof(GattCharacteristic *));

        ble.gattServer().addService(environmentalService);
        serviceAdded = true;
    }

    /**
     * @brief   Update humidity characteristic.
     * @param   newHumidityVal New humidity measurement.
     */
    void updateHumidity(HumidityType_t newHumidityVal)
    {
        humidity = (HumidityType_t) (newHumidityVal);
        ble.gattServer().write(humidityCharacteristic.getValueHandle(), (uint8_t *) &humidity, sizeof(HumidityType_t));
    }

    /**
     * @brief   Update pressure characteristic.
     * @param   newPressureVal New pressure measurement.
     */
    void updatePressure(PressureType_t newPressureVal)
    {
        pressure = (PressureType_t) (newPressureVal * 10);
        ble.gattServer().write(pressureCharacteristic.getValueHandle(), (uint8_t *) &pressure, sizeof(PressureType_t));
    }

    /**
     * @brief   Update temperature characteristic.
     * @param   newTemperatureVal New temperature measurement.
     */
    void updateTemperature(float newTemperatureVal)
    {
        temperature = (TemperatureType_t) (newTemperatureVal * 100);
        ble.gattServer().write(temperatureCharacteristic.getValueHandle(), (uint8_t *) &temperature, sizeof(TemperatureType_t));
    }

private:
    BLE& ble;

    TemperatureType_t temperature;
    HumidityType_t    humidity;
    PressureType_t    pressure;

    ReadOnlyGattCharacteristic<TemperatureType_t> temperatureCharacteristic;
    ReadOnlyGattCharacteristic<HumidityType_t>    humidityCharacteristic;
    ReadOnlyGattCharacteristic<PressureType_t>    pressureCharacteristic;
};

#endif /* #ifndef __BLE_ENVIRONMENTAL_SERVICE_H__*/