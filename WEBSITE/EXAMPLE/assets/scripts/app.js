// Initialize Firebase
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-app.js"
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-auth.js"
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-database.js"
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-firestore.js"
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-messaging.js"
src="https://www.gstatic.com/firebasejs/5.8.4/firebase-functions.js"

const firebaseApp = firebase.initializeApp({
  apiKey: "AIzaSyC-vZnOFnjnZbIHfMJFaXPhPSxY65w64uM",
  authDomain: "iotssc.firebaseapp.com",
  databaseURL: "https://iotssc.firebaseio.com",
  projectId: "iotssc",
  storageBucket: "iotssc.appspot.com",
  messagingSenderId: "628664425338"
});



/*var config = {
    apiKey: "AIzaSyC-vZnOFnjnZbIHfMJFaXPhPSxY65w64uM",
    authDomain: "iotssc.firebaseapp.com",
    databaseURL: "https://iotssc.firebaseio.com",
    projectId: "iotssc",
    storageBucket: "iotssc.appspot.com",
    messagingSenderId: "628664425338"
  };
  firebase.initializeApp(config);
*/

/*
const auth = firebaseApp.auth();
const db = firebaseApp.firestore();
db.collection("PIR_IN").get().then(function(querySnapshot) {
querySnapshot.forEach(function(doc) {
    console.log(doc.id, " => ", doc.data());
});
*/
