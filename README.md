# Running App V2 - Mobile App Development (Kotlin)

## App Functionality

This version of the running app allows users to register using Firebase Auth (email & password / Google Sign In flows supported). Once signed up / logged in, users can add races with support for race images (stored on Firebase Cloud Storage) and race location (via placement of Google maps marker on map view). Users can favourite existing races in the app. Extensive support for filtering is present (via a searchview which filters by race titles, via switches which filter by user created races / user favourited races). Users can edit / delete races they have created. Night mode is also supported.

- CRUD support is enabled for race records.

- Google Maps API is used to provide users with a visual representation of existing races on a map view as well as allowing users to select the location of races when adding new records (by dragging a Google Maps marker).

- Firebase Realtime Database is used to persist records for users. 

- Firebase Cloud Storage is used to store both the user avatar profile picture and any pictures added by users for race records.

- Firebase Auth is used to provide users with a quicker method of signing up to the app (via Google Sign In)

## UML & Class Diagrams

### App Components

![](https://github.com/JohnDennehy101/Running-App-V2/blob/readme/app/readme/diagram.png?raw=true=250x250)

### Firebase Authentication

Both email and Google Sign in are supported within Firebase Authentication.

![](https://github.com/JohnDennehy101/Running-App-V2/blob/readme/app/readme/firebase_auth.png?raw=true=250x250)

### Firebase Realtime Database Root Structure

At the root level, there are 2 properties (races & user-races).

Under races, each individual race is stored (with the unique race uid as the identifier).

Under user-races, each individual user uid is stored (if a user has created more than one race, they will have multiple race uid records under their Firebase uid).

![](https://github.com/JohnDennehy101/Running-App-V2/blob/readme/app/readme/firebase_root_schema.png?raw=true=250x250)

### Firebase Realtime Database - Individual race record within races (each record stored under unique race record uid)

Favourited By property stores array of user uids who have favourited the race record. Likewise for createdUser and updatedUser (Firebase Auth uid of relevant user stored in these properties).

![](https://github.com/JohnDennehy101/Running-App-V2/blob/readme/app/readme/races_schema.png?raw=true=250x250)

### Firebase Realtime Database - Individual race record within user-races (races categorised under uid of user that created race)

![](https://github.com/JohnDennehy101/Running-App-V2/blob/readme/app/readme/user_races_schema.png?raw=true=250x250)

## UX Approach Adopted

- Material UI library was used for pre-built app components to provide users with a seamless UX.
- MVVM design pattern was used for the second iteration of the app.
- Swipe functionality is present (swiping left on a user's own race allows for deletion of the record, swiping right on a user created race brings the user to the edit race view with the race info pre-populated).
- Following registration / login, the user is presented with a navigation architecture component which allows the user to quickly navigate between app fragments.
- A variety of UI elements are in place to provide a rich and aesthetically pleasing UI to the user (date picker, text input layouts, toggle button groups, switches etc.).
- Animations and transitions ensure a seamless change of views within the app.
- Presence of theme selection - night mode option provides the user with the ability to change the UI which could help to reduce eye strain and the app's impact on the mobile device's battery life.


## Git Approach

An extensive commit history is in place which demonstrates an iterative development process. Tagged releases provide a snapshot of the app functionality which was shipped by assignment deadlines. A develop and release branch are present to ensure that changes are not committed directly on the main branch.

## Personal Statement

I decided to develop an app for managing running races as I regularly participate in races around Ireland. I thought this assignment provided a good opportunity to develop an app which I could use to store races that I am interested in participating in during 2022 and beyond.

## References

The lecture slides and labs for this module were used as a base reference for many of the features implemented in this application. In addition to these, the below references provided useful guidance:

- Alifio Tutorials (2019) Search Bar + RecyclerView+Firebase Realtime Database easy Steps [video], available: https://www.youtube.com/watch?v=PmqYd-AdmC0 [Accessed 05 December 2021]
- Android - How to filter the RecyclerView of Firebase? StackOverflow, available: https://stackoverflow.com/questions/45966414/android-how-to-filter-the-recyclerview-of-firebase [Accessed 04 December 2021]
- Android Material Button Toggle Group - Check None Selected, StackOverflow, available: https://stackoverflow.com/questions/60986878/android-material-button-toggle-group-check-none-selected [Accessed 11 December 2021]
- Change the On/Off text of a toggle button Android, StackOverflow, available at: https://stackoverflow.com/questions/8673347/change-the-on-off-text-of-a-toggle-button-android [Accessed December 10 2021]
- ConstraintLayout: place TextView below ImageView, StackOverflow, available at: https://stackoverflow.com/questions/47179017/constraintlayout-place-textview-below-imageview/47179211 [Accessed 08 December 2021]
- create favourite button using toogleButton, StackOverflow, available: https://stackoverflow.com/questions/43961023/create-favourite-button-using-tooglebutton [Accessed 12 December 2021]
- Getting currently logged-in user's ID in Firebase Auth in Kotlin StackOverflow, available: https://stackoverflow.com/questions/59117267/getting-currently-logged-in-users-id-in-firebase-auth-in-kotlin [Accessed 04 December 2021]
- Google map for android my location custom button, StackOverflow, available at: https://stackoverflow.com/questions/23883235/google-map-for-android-my-location-custom-button [Accessed December 12 2021]
- How can I make a Google Maps custom Overlay object behave like an InfoWindow?, StackOverflow, available: https://stackoverflow.com/questions/532320/how-can-i-make-a-google-maps-custom-overlay-object-behave-like-an-infowindow [Accessed 10 December 2021]
- How do I hide a menu item in the actionbar?, StackOverflow, available: https://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar [Accessed 13 December 2021]
- How to add buttons at top of map fragment API v2 layout, StackOverflow, available at: https://stackoverflow.com/questions/14694119/how-to-add-buttons-at-top-of-map-fragment-api-v2-layout/14706956#14706956 [Accessed 14 December 2021]
- How to add layout over google maps?, StackOverflow, available: https://stackoverflow.com/questions/32075153/how-to-add-layout-over-google-maps [Accessed 13 December 2021]
- How to Hide Map Fragment - Android, StackOverflow, available at: https://stackoverflow.com/questions/28297648/how-to-hide-map-fragment-android [Accessed December 13 2021]
- How to toggle between day and night themes programmatically using Kotlin, Coders Guidebook, available at: https://codersguidebook.com/how-to-create-an-android-app/day-night-android-themes [Accessed December 15 2021]
- Kamal, F. (2019) ConstraintLayout Tutorial for Android: Complex Layouts, raywenderlich.com, available at: https://www.raywenderlich.com/9475-constraintlayout-tutorial-for-android-complex-layouts [Accessed December 12 2021]
- Khan, U. (2019), Implementing Dark Theme in Android, Prototypr.io, available: https://blog.prototypr.io/implementing-dark-theme-in-android-dfe63e62145d [Accessed 14 December 2021]
- MaterialButtonToggleGroup, Android Developers, available: https://developer.android.com/reference/com/google/android/material/button/MaterialButtonToggleGroup [Accessed 11 December 2021]
- Move a View with Animation, Android Developers, available: https://developer.android.com/training/animation/reposition-view [Accessed 14 December 2021]
- Olukoye, H. (2020) Display Data From Firebase Firestore in Android RecyclerView, Medium, available: https://medium.com/quick-code/display-data-from-firebase-firestore-in-android-recyclerview-db39f8c7d6b [Accessed 06 December 2021]
- Read and Write Data on Android Google, available: https://firebase.google.com/docs/database/android/read-and-write [Accessed 03 December 2021]
- Trying to show Google Maps inside fragment dynamically, error inflating class fragment, StackOverflow, available: https://stackoverflow.com/questions/40200707/trying-to-show-google-maps-inside-fragment-dynamically-error-inflating-class-fr [Accessed 13 December 2021]
- Remove a marker from a GoogleMap, StackOverflow, available at: https://stackoverflow.com/questions/13692398/remove-a-marker-from-a-googlemap [Accessed December 14 2021]
- What is the difference between gravity and layout_gravity in Android?, StackOverflow, available at: https://stackoverflow.com/questions/3482742/what-is-the-difference-between-gravity-and-layout-gravity-in-android [Accessed December 11 2021]





