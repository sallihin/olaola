# olaola music player
A music app for kids, built with Java on Android Studios. "Ola Ola" is a filler word that my 3 year old daughter uses when she's unsure what to say. I didn't know what to call my app, so let's call it "Ola Ola". Ola coincidentally means wave in Spanish. Like sound waves! 💡

## Tasks / Features
- [x] Proposed GUI on XD
- [x] App Login via Firebase Authentication
- [x] Store/Retrieve data from Firebase Realtime Database
- [x] Slide transitions 
- [x] Swipe left/right to change music 
- [x] Scrub songs 
- [x] Replay music
- [x] Shuffle music 
- [x] Swipe to change songs 
- [x] Store additional user details on Firebase 
- [x] Display unique user name on Song Listing Activity


## Notes
- The uneven songlist was done intentionally to display the dynamics of the 2 column layout.
- Swipe left/right on the player screen to change songs
- Swipe down on the player screen to go back to the song list 


## Proposed GUI 
I designed the app on Adobe XD. The initial design contains additional functions which was later not included due to time constraints. 

[[ View the prototype here ]](https://xd.adobe.com/view/e5575c5b-b6d6-4e50-ae59-04d8a4180cda-b004/)

<img src="http://music-app-bf8f9.web.app/img/ola-ola-gui-overview.png" alt="ola-ola-gui" width="80%"/>

## Bugs Found
- [x] Tapping on the coverArt multiple times causes startActivity animation to appear 
- [ ] When going prevActivity from the player, the current screen blinks for a second
- [ ] Pressing next/previous, the current coverArt disappears instead of slides out
- [ ] Sometimes the shuffle song lands on the same song again

## Visit my Instagram! 
I've also documented the programming process (and my #100daysofcode journey) on my instagram page [mmw.codes](https://instagram.com/mmw.codes)


### Attribution
* [Monster Vectors](https://www.freepik.com/vectors/character) 
* [GeeksforGeeks](https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/)

### Libraries/Resources/Tutorials 
* [Picasso](https://square.github.io/picasso/#download)  
* [Firebase Documentation](https://firebase.google.com/docs/libraries) 
* [Firebase Authentication Setup](https://www.youtube.com/watch?v=TwHmrZxiPA8) 
