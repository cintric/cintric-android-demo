# cintric-android-demo
A Cintric app for android which implements the Cintric SDK and shows your location history

**To get started:**

1. Download this project and open it in Android Studio. 

2. Register for a Cintric developer account to get your SDK key and Secret: http://cintric.com/register 
https://cintric.com/docs/android#register   
 
3. In the projects MainActivity, update this line 
`Cintric.startCintricService(this, "YOUR-SDK-KEY", "YOUR-SECRET");` 
to contain your Cintric SDK key and Secret. 

4. Update the strings.xml resource file to contain your google app ID (you can get one here: https://console.developers.google.com/)
https://cintric.com/docs/android#add-google-app-id  
 
5. Update your google_maps_api.xml resource file to contain your google maps API Key. 
Follow the instructions here to get one https://developers.google.com/maps/documentation/android-api/start

 
**The app will look something like this:**
<img src="https://storage.googleapis.com/cdn.cintric.com/img/github/android-docs/cintric-android-demo-app-screenshot.png"  width="225" height="400" alt="Cintric Android Demo App Screenshot">