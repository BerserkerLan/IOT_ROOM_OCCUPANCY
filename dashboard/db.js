<script src="https://www.gstatic.com/firebasejs/5.8.0/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/5.8.0/firebase-firestore.js"></script>
<script src="https://www.gstatic.com/firebasejs/5.8.4/firebase.js"></script>

function initialize(){
  document.write("Initialize function is called ");
  // Initialize Firebase
  /var config = {
    apiKey: "AIzaSyC-vZnOFnjnZbIHfMJFaXPhPSxY65w64uM",
    authDomain: "iotssc.firebaseapp.com",
    databaseURL: "https://iotssc.firebaseio.com",
    projectId: "iotssc",
    storageBucket: "iotssc.appspot.com",
    messagingSenderId: "628664425338"
  };
  firebase.initializeApp(config); 
}

function genFunction() {

  document.write("TEST1");
  var docRef = db.collection("PROCESSED").doc("24022019");
  var getOptions = {
    source: 'cache'
  };
  docRef.get().then(function(doc) {
    // Document was found in the cache. If no cached document exists,
    // an error will be returned to the 'catch' block below.i
  //  document.write(doc.data());
        console.log("Document data:", doc.data());
  }).catch(function(error) {
  //document.write("Error");
        console.log("Document data:", "here");
  });
  document.write("TEST2");
}
