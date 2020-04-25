**A small library to create long-running broadcast receiver**

**Run services from receiver in the background and automatic obtaining Wakelocks**

This library based on the code given in Chapter 19 of the book Pro Android 4
Authors: Komatineni, Satya, MacLean, Dave

https://www.apress.com/gp/book/9781430239307

OR

http://www.williamspublishing.com/Books/978-5-8459-1801-7.html

Also contains code to work with the Alarm manager which is based on the article
https://habr.com/post/274169/

See [JavaDocs](https://dmitrystarkin.github.io/Long-running-service/)

Usage:

1 in project level build.gradle add:
```
repositories {
........
        maven { url "https://jitpack.io" }
   }
```

2 in module level build.gradle add:
```
dependencies {
...........
         implementation 'com.github.DmitryStarkin:Long-running-service:1.2.0b'
   }
```

3 Inherit from the abstract class LongRunningBroadcastService and implement them
 single method handleIntent, your service must have a default constructor and call
 the superclass constructor and pass it the name. Example:

 ```Java
 public class MyService extends LongRunningBroadcastService {

 public MyService() {

        super("MyService");
    }
 ...

  /**
     * Perform long running operations in this method.
     * This is executed in a separate thread.
     */
    @Override
    protected boolean handleIntent(Intent handledIntent)
    {
       Utils.logThreadSignature(tag);
       Log.d(tag,"Sleeping for 60 secs");
       Utils.sleepForInSecs(60);
       String message =
          handledIntent.getStringExtra("message");
       Log.d(tag,"Job completed");
       Log.d(tag,message);
    }
 }
 ...
 }
 ```

4 Then register the service in the manifest file.
 ```
 <application
 ...
 <service android:name="com....MyService"/>
 ...
 </application>
 ```
5  Inherit from the abstract class LongRunningReceiver and implement the single method getServiceClass()
(You must return the class of Your service). Example:
 ```Java
 public class MyReceiver extends LongRunningReceiver {

     private final String TAG = getClass().getSimpleName();

     @Override
     public Class getServiceClass() {

         if (DEBUG) {
             Log.d(TAG, "getServiceClass: ");
         }
         return MyService.class;
     }
 }

 ```

6 Then register the broadcast receiver in the
 manifest file.

 ```
   <application
  ...
  <receiver android:name="com.....MyReceiver">
              <intent-filter>
                  <action android:name="android.intent.action...."/>
              </intent-filter>
          </receiver>
  ...
  </application>
  ```
  You are finished.

  To start the service without using a receiver
  ```java
  Intent newIntent = new Intent(this.getApplicationContext(), MyService.class);
              startService(newIntent);
  ```

 This library registers the following permission in its manifest

 ```
 <uses-permission android:name="android.permission.WAKE_LOCK"/>
 ```

To work with the Alarm manager use the static methods from the class AlarmManagerUtil