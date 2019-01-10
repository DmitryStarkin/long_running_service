**A small library to create long-running broadcast receiver**

**Run services in the background and automatic obtaining Wakelocks**

This library based on the code given in Chapter 19 of the book Pro Android 4
Authors: Komatineni, Satya, MacLean, Dave

https://www.apress.com/gp/book/9781430239307

OR

http://www.williamspublishing.com/Books/978-5-8459-1801-7.html

Also contains code to work with the Alarm manager which is based on the article
https://habr.com/post/274169/


Usage:

1 add a module to the project

2 in build.gradle write -  implementation project(':intetServiseUtil')

3  Inherit from the abstract class LongRunningBroadcastService and implement them
 single method handleIntent, your service must have a default constructor and call
 the superclass constructor and pass it the name: for example

 ```
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
(You must return the class of Your service): for example
 ```
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

7  Register a user permission to work with the wake lock.
 ```
 <uses-permission android:name="android.permission.WAKE_LOCK"/>
 ```
  You are finished.

  To start the service without using a receiver
  ```
  Intent newIntent = new Intent(this.getApplicationContext(), MyService.class);
              startService(newIntent);
  ```

To work with the Alarm manager use the static methods from the class AlarmManagerUtil