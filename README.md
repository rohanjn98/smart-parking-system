# smart-parking-system

The project provides an organised parking solution to automate the existing process of parking in India.


## How it works

1. Real time data collected from the sensors deployed across different parking systems (containing parking slots) is uploaded on Google's Firebase real time database. 
2. Using this Firebase real time database, availability of parking slots in a system is provided to the user in an android app (consisting of Google Maps API). 
3. The user can book a slot near him as parking systems with available parking slots were shown with colored markers in the app.
4. Once the user confirms his booking, a QR code is generated by the app which is to be scanned on reaching the parking space.
5. The user can then either navigate himself/herself to the destination or can ask the app to provide the navigation.
6. Once the user reaches the destination (parking space), he/she needs to provide the auto-generated QR in the app for scanning.
7. The system then scanes the QR and allots the nearest slot available to the user.
8. Once the user parks his/her car, the sensors deployed send data to the microcontroller only as long as the car is present.
9. When the user is leaving, he/she needs to scan the QR code again to generate billing details. So, the user is charged only for the stipulated amount of time he/she used the parking slot.
10. Since parking bills are very low, we plan to incorporate in-app payment solutions so that users can pay their parking bills on monthly basis.
11. Provision is also made for the user to rent out available space for parking that he/she owns.
12. Sensors are required if we wish to provide slot numbers to users on their arrival or to check which slot is available. The use of sensors can also be avoided as we already use two time scanning process that helps us to evaluate total time the vehicle is parked and the bill can be generated based on the time difference between the two scans.
13. The barricade opens only after successful scanning of the QR code and the code expires after two time scan and even if it isn't used within 2-3hrs. This is done to avoid blocking of slot.
