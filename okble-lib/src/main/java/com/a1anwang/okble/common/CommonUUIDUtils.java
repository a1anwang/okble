package com.a1anwang.okble.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a1anwang.com on 2017/6/27.
 * contact：www.a1anwang.com
 */

public class CommonUUIDUtils {

    public final static String CommonUUIDStr_x = "0000xxxx-0000-1000-8000-00805f9b34fb";

    public final static String Client_Characteristic_Configuration="00002902-0000-1000-8000-00805f9b34fb";
    public final static String Battery_Level="00002a19-0000-1000-8000-00805f9b34fb";


    private static Map<String,String> uuidDescMap=new HashMap<>();


    static {
        // Sample Services.
        uuidDescMap.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        uuidDescMap.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        uuidDescMap.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        uuidDescMap.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");

        // GATT Services
        uuidDescMap.put("00001800-0000-1000-8000-00805f9b34fb", "GenericAccess");
        uuidDescMap.put("00001801-0000-1000-8000-00805f9b34fb", "GenericAttribute");

        // GATT Declarations
        uuidDescMap.put("00002800-0000-1000-8000-00805f9b34fb", "Primary Service");
        uuidDescMap.put("00002801-0000-1000-8000-00805f9b34fb", "Secondary Service");
        uuidDescMap.put("00002802-0000-1000-8000-00805f9b34fb", "Include");
        uuidDescMap.put("00002803-0000-1000-8000-00805f9b34fb", "Characteristic");

        // GATT Descriptors
        uuidDescMap.put("00002900-0000-1000-8000-00805f9b34fb", "Characteristic Extended Properties");
        uuidDescMap.put("00002901-0000-1000-8000-00805f9b34fb", "Characteristic User Description");
        uuidDescMap.put("00002902-0000-1000-8000-00805f9b34fb", "Client Characteristic Configuration");
        uuidDescMap.put("00002903-0000-1000-8000-00805f9b34fb", "Server Characteristic Configuration");
        uuidDescMap.put("00002904-0000-1000-8000-00805f9b34fb", "Characteristic Presentation Format");
        uuidDescMap.put("00002905-0000-1000-8000-00805f9b34fb", "Characteristic Aggregate Format");
        uuidDescMap.put("00002906-0000-1000-8000-00805f9b34fb", "Valid Range");
        uuidDescMap.put("00002907-0000-1000-8000-00805f9b34fb", "External Report Reference Descriptor");
        uuidDescMap.put("00002908-0000-1000-8000-00805f9b34fb", "Report Reference Descriptor");

        // GATT Characteristics
        uuidDescMap.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        uuidDescMap.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        uuidDescMap.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        uuidDescMap.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        uuidDescMap.put("00002a04-0000-1000-8000-00805f9b34fb", "PPCP");
        uuidDescMap.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");

        // GATT Service UUIDs
        uuidDescMap.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        uuidDescMap.put("00001803-0000-1000-8000-00805f9b34fb", "Link Loss");
        uuidDescMap.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
        uuidDescMap.put("00001805-0000-1000-8000-00805f9b34fb", "Current Time Service");
        uuidDescMap.put("00001806-0000-1000-8000-00805f9b34fb", "Reference Time Update Service");
        uuidDescMap.put("00001807-0000-1000-8000-00805f9b34fb", "Next DST Change Service");
        uuidDescMap.put("00001808-0000-1000-8000-00805f9b34fb", "Glucose");
        uuidDescMap.put("00001809-0000-1000-8000-00805f9b34fb", "Health Thermometer");
        uuidDescMap.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information");
        uuidDescMap.put("0000180b-0000-1000-8000-00805f9b34fb", "Network Availability");
        uuidDescMap.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate");
        uuidDescMap.put("0000180e-0000-1000-8000-00805f9b34fb", "Phone Alert Status Service");
        uuidDescMap.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        uuidDescMap.put("00001810-0000-1000-8000-00805f9b34fb", "Blood Pressure");
        uuidDescMap.put("00001811-0000-1000-8000-00805f9b34fb", "Alert Notification Service");
        uuidDescMap.put("00001812-0000-1000-8000-00805f9b34fb", "Human Interface Device");
        uuidDescMap.put("00001813-0000-1000-8000-00805f9b34fb", "Scan Parameters");
        uuidDescMap.put("00001814-0000-1000-8000-00805f9b34fb", "Running Speed and Cadence");
        uuidDescMap.put("00001816-0000-1000-8000-00805f9b34fb", "Cycling Speed and Cadence");
        uuidDescMap.put("00001818-0000-1000-8000-00805f9b34fb", "Cycling Power");
        uuidDescMap.put("00001819-0000-1000-8000-00805f9b34fb", "Location and Navigation");

        // GATT Characteristic UUIDs
        uuidDescMap.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level");
        uuidDescMap.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
        uuidDescMap.put("00002a08-0000-1000-8000-00805f9b34fb", "Date Time");
        uuidDescMap.put("00002a09-0000-1000-8000-00805f9b34fb", "Day of Week");
        uuidDescMap.put("00002a0a-0000-1000-8000-00805f9b34fb", "Day Date Time");
        uuidDescMap.put("00002a0c-0000-1000-8000-00805f9b34fb", "Exact Time 256");
        uuidDescMap.put("00002a0d-0000-1000-8000-00805f9b34fb", "DST Offset");
        uuidDescMap.put("00002a0e-0000-1000-8000-00805f9b34fb", "Time Zone");
        uuidDescMap.put("00002a0f-0000-1000-8000-00805f9b34fb", "Local Time Information");
        uuidDescMap.put("00002a11-0000-1000-8000-00805f9b34fb", "Time with DST");
        uuidDescMap.put("00002a12-0000-1000-8000-00805f9b34fb", "Time Accuracy");
        uuidDescMap.put("00002a13-0000-1000-8000-00805f9b34fb", "Time Source");
        uuidDescMap.put("00002a14-0000-1000-8000-00805f9b34fb", "Reference Time Information");
        uuidDescMap.put("00002a16-0000-1000-8000-00805f9b34fb", "Time Update Control Point");
        uuidDescMap.put("00002a17-0000-1000-8000-00805f9b34fb", "Time Update State");
        uuidDescMap.put("00002a18-0000-1000-8000-00805f9b34fb", "Glucose Measurement");
        uuidDescMap.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        uuidDescMap.put("00002a1c-0000-1000-8000-00805f9b34fb", "Temperature Measurement");
        uuidDescMap.put("00002a1d-0000-1000-8000-00805f9b34fb", "Temperature Type");
        uuidDescMap.put("00002a1e-0000-1000-8000-00805f9b34fb", "Intermediate Temperature");
        uuidDescMap.put("00002a21-0000-1000-8000-00805f9b34fb", "Measurement Interval");
        uuidDescMap.put("00002a22-0000-1000-8000-00805f9b34fb", "Boot Keyboard Input Report");
        uuidDescMap.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        uuidDescMap.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        uuidDescMap.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        uuidDescMap.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        uuidDescMap.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        uuidDescMap.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        uuidDescMap.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        uuidDescMap.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory");
        uuidDescMap.put("00002a2b-0000-1000-8000-00805f9b34fb", "Current Time");
        uuidDescMap.put("00002a31-0000-1000-8000-00805f9b34fb", "Scan Refresh");
        uuidDescMap.put("00002a32-0000-1000-8000-00805f9b34fb", "Boot Keyboard Output Report");
        uuidDescMap.put("00002a33-0000-1000-8000-00805f9b34fb", "Boot Mouse Input Report");
        uuidDescMap.put("00002a34-0000-1000-8000-00805f9b34fb", "Glucose Measurement Context");
        uuidDescMap.put("00002a35-0000-1000-8000-00805f9b34fb", "Blood Pressure Measurement");
        uuidDescMap.put("00002a36-0000-1000-8000-00805f9b34fb", "Intermediate Cuff Pressure");
        uuidDescMap.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        uuidDescMap.put("00002a38-0000-1000-8000-00805f9b34fb", "Body Sensor Location");
        uuidDescMap.put("00002a39-0000-1000-8000-00805f9b34fb", "Heart Rate Control Point");
        uuidDescMap.put("00002a3e-0000-1000-8000-00805f9b34fb", "Network Availability");
        uuidDescMap.put("00002a3f-0000-1000-8000-00805f9b34fb", "Alert Status");
        uuidDescMap.put("00002a40-0000-1000-8000-00805f9b34fb", "Ringer Control Point");
        uuidDescMap.put("00002a41-0000-1000-8000-00805f9b34fb", "Ringer Setting");
        uuidDescMap.put("00002a42-0000-1000-8000-00805f9b34fb", "Alert Category ID Bit Mask");
        uuidDescMap.put("00002a43-0000-1000-8000-00805f9b34fb", "Alert Category ID");
        uuidDescMap.put("00002a44-0000-1000-8000-00805f9b34fb", "Alert Notification Control Point");
        uuidDescMap.put("00002a45-0000-1000-8000-00805f9b34fb", "Unread Alert Status");
        uuidDescMap.put("00002a46-0000-1000-8000-00805f9b34fb", "New Alert");
        uuidDescMap.put("00002a47-0000-1000-8000-00805f9b34fb", "Supported New Alert Category");
        uuidDescMap.put("00002a48-0000-1000-8000-00805f9b34fb", "Supported Unread Alert Category");
        uuidDescMap.put("00002a49-0000-1000-8000-00805f9b34fb", "Blood Pressure Feature");
        uuidDescMap.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information");
        uuidDescMap.put("00002a4b-0000-1000-8000-00805f9b34fb", "Report Map");
        uuidDescMap.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point");
        uuidDescMap.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report");
        uuidDescMap.put("00002a4e-0000-1000-8000-00805f9b34fb", "Protocol Mode");
        uuidDescMap.put("00002a4f-0000-1000-8000-00805f9b34fb", "Scan Interval Window");
        uuidDescMap.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
        uuidDescMap.put("00002a51-0000-1000-8000-00805f9b34fb", "Glucose Feature");
        uuidDescMap.put("00002a52-0000-1000-8000-00805f9b34fb", "Record Access Control Point");
        uuidDescMap.put("00002a53-0000-1000-8000-00805f9b34fb", "RSC Measurement");
        uuidDescMap.put("00002a54-0000-1000-8000-00805f9b34fb", "RSC Feature");
        uuidDescMap.put("00002a55-0000-1000-8000-00805f9b34fb", "SC Control Point");
        uuidDescMap.put("00002a5b-0000-1000-8000-00805f9b34fb", "CSC Measurement");
        uuidDescMap.put("00002a5c-0000-1000-8000-00805f9b34fb", "CSC Feature");
        uuidDescMap.put("00002a5d-0000-1000-8000-00805f9b34fb", "Sensor Location");
        uuidDescMap.put("00002a63-0000-1000-8000-00805f9b34fb", "Cycling Power Measurement");
        uuidDescMap.put("00002a64-0000-1000-8000-00805f9b34fb", "Cycling Power Vector");
        uuidDescMap.put("00002a65-0000-1000-8000-00805f9b34fb", "Cycling Power Feature");
        uuidDescMap.put("00002a66-0000-1000-8000-00805f9b34fb", "Cycling Power Control Point");
        uuidDescMap.put("00002a67-0000-1000-8000-00805f9b34fb", "Location and Speed");
        uuidDescMap.put("00002a68-0000-1000-8000-00805f9b34fb", "Navigation");
        uuidDescMap.put("00002a69-0000-1000-8000-00805f9b34fb", "Position Quality");
        uuidDescMap.put("00002a6a-0000-1000-8000-00805f9b34fb", "LN Feature");
        uuidDescMap.put("00002a6b-0000-1000-8000-00805f9b34fb", "LN Control Point");

        //OAD service
        uuidDescMap.put("f000ffc0-0451-4000-b000-000000000000", "OAD Service");
        //OAD Characteristics
        uuidDescMap.put("f000ffc1-0451-4000-b000-000000000000", "Image Identify");
        uuidDescMap.put("f000ffc2-0451-4000-b000-000000000000", "Image Block");
        uuidDescMap.put("f000ffc5-0451-4000-b000-000000000000", "Image Control");

    }

    public static String getUUIDDesc(String completeUUID){
        if(uuidDescMap.containsKey(completeUUID.toLowerCase())){
            return uuidDescMap.get(completeUUID);
        }else{
            return "Unknown";
        }

    }


}
