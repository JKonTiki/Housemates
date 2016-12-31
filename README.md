# Housemates


Housemates was born of a simple question: _Are my roommates home?_ Whether it's a thought arising from sleepy isolation in your room or for having forgotten to turn off the stove, Housemates is intended to answer that question without needing to ask it. The application collects location data from users and, without divulging anything more, simply informs them of whether the roommates they've signed up with are home or not.


![Login Page Screenshot](Screenshots/LoginActivity.png?raw=true "Login Page")![Hom Page Screenshot](Screenshots/MainActivity.png?raw=true "Home Page")![Navigation Page Screenshot](Screenshots/NavDrawer.png?raw=true "Navigation Page")


### Setup

A simple and relatively accessible way to launch this app:

* Open or [download](https://developer.android.com/studio/index.html) Android Studio
* Clone this repository to your desktop
* Open project directory through Android Studio
* [Set up an emulator](https://developer.android.com/studio/run/managing-avds.html) and launch the app!


### Functionality

* Users can create an account or sign in if they've already done so
* When a user signs in, they are sent straight to the home page of the app (MainActivity), where they see a list of roommates in the house(s) they belong to and their "at home" status
* Android's Location Services monitor the user's location, sending a switch to the database when a user has entered within ~.2 miles of a house that they belong to
* If user does not yet belong to any houses, they are presented with a message suggesting they either create or join a house
* Through a menu opened from the left-hand side of the home page, users can navigate to other activities
* From all other activities (available to a signed-in user), user can use an action bar back button to return home
* Users can create a virtual house, choosing a name and setting its address by their current location
* Users have the option to view a special passkey for the house that they belong to and click a button to share it via a preformatted text
* Passkeys can be entered to join preexisting houses
* any roommates' "at home" status will automatically update when they enter or leave the area declared as their house


### Database Structure

- Houses 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- GU2P9F
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • houseCode: "GU2P9F" 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • latitude: "38.8977" 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • longitude: "-77.0365"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • name: "The White House" 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        -  roommates 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            • "9IdtpQ1NDmZUcTFFQZRou1Vk9aR2"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            • "RDssttmfyUaFzWLC0NuT1QvnXUZ2"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            • "OpUJ7JGXNwOxGBPXyyZLNlYH7Cf2"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            • "pvKbmNtkesQbVvI8Ds4UM5HHey12"

- Roommates 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    - "9IdtpQ1NDmZUcTFFQZRou1Vk9aR2"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • atHome: "true"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        -  houseIds 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            • "GU2P9F"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • name: "Barrack"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        • roommateId: "9IdtpQ1NDmZUcTFFQZRou1Vk9aR2" 


### Improvement Needed

The project is still in development and would certainly benefit from additional features and code tuneups. Under the hood, the application does have a bit of an abundance of monolithic code. My first major project in Android, Housemates was as much of a vehicle for my exploration and discovery as it was a product itself, and so it would benefit from more modularization (particularly for scalabiltiy of certain features), refactoring to better fit conventional organization and structuring, and general retrospect.


Features to be added and improved:
* As of now, users can only set a house's location by their Current Location. They should also have the option of [entering an address](https://developers.google.com/maps/documentation/geocoding/intro) or [dropping a pin on a map](https://developers.google.com/maps/documentation/android-api/map)
* Gathering of the user's location should be refactored as a service running throughout all activities and made more reliable as a background process
* Data has been structured to allow for users to belong to multiple houses, although the interface has not yet been made scalable in this way (the user's first house is currently the only of shown). Would simply mean nesting the homepage's list of roommates (belonging to a house) inside a list of houses
* General edit and delete functionality. Users should be able to leave a house, edit their account information, etc.
* More account info, including an avatar to be selected during account creation that represents user's profile on others' devices and as their active account
* [ChildEventListener](https://www.firebase.com/docs/java-api/javadoc/com/firebase/client/ChildEventListener.html) for list of user's roommates, which currently updates status of existing roommates but does not yet _live_ update new roommates added to their house
* User's should not experience lag when the app calls to Firebase for data, so basically the MainActivity should more smoothly set its content view once data has been retrieved. Data should also be cached more efficiently
* In the "Roommates" branch of the database, "atHome" should be recorded per house (through booleans in child houseId array), not as a general status of the roommate
* "Forgot Password" option for resetting password


### Potential for Expansion

Housemates is meant to be simple, to not distract or deter by avoiding too many bells and whistles. That being said, any app that draws in users with guarantees of simplicity ought to also be ready to offer them the services they might hope to find in the same place. While Housemates' core premise is simple, it could certainly broaden itself to become more of a virtual hub for all matters of cohabitation (discussion, notes, bills, etc).


### Credits

Many thanks to [this tutorial](https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639) on constructing geofences, Google's spectacular documentation, and of course the infinite support of Stack Overflow. And Barrack Obama.

This software is licensed under the MIT license.
Copyright (c) 2016 Jeremy Fryd.