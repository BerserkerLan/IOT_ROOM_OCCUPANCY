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


function getMostPopularDay(){
  var db = firebaseApp.firestore();
  const comments = [];
  //This will get the most popular day
  var docRef = db.collection("AUX").doc("MOST_POPULAR_DAY");
  docRef.get().then(function(doc) {
  if (doc.exists) {
      console.log("Document data:", doc.data());
      const MOST_POPULAR_DAY = doc.data()['Day'];
      return MOST_POPULAR_DAY;
     // document.write(comment);
  } else {
      console.log("No such document!");
  }
  }).catch(function(error) {
  console.log("Error getting document:", error);
  });
}

function getMostPopularMonth(){
  var db = firebaseApp.firestore();
  const comments = [];
  //This will get the most popular month
  var docRef = db.collection("AUX").doc("MOST_POPULAR_MONTH");
  docRef.get().then(function(doc) {
  if (doc.exists) {
      console.log("Document data:", doc.data());
      const MOST_POPULAR_MONTH = doc.data()['Month'];
      return MOST_POPULAR_MONTH;
  } else {
      console.log("No such document!");
  }
  }).catch(function(error) {
  console.log("Error getting document:", error);
  });
}

function getMostPopularTime(){
  var db = firebaseApp.firestore();
  const comments = [];
  //This will get the most popular time
  var docRef = db.collection("AUX").doc("MOST_POPULAR_TIME");
  docRef.get().then(function(doc) {
  if (doc.exists) {
      console.log("Document data:", doc.data());
      const MOST_POPULAR_TIME = doc.data()['Time'];
      return MOST_POPULAR_TIME;
     // document.write(comment);
  } else {
      console.log("No such document!");
  }
  }).catch(function(error) {
  console.log("Error getting document:", error);
 });
}

function getMonthlyStats(){
  var db = firebaseApp.firestore();
  const comments = [];
  //This will get the most popular time
  var docRef = db.collection("PROCESSED").doc("24022019");
  docRef.get().then(function(doc) {
  if (doc.exists) {
    //console.log("DOC DATA" + )
    //console.log("DOC DATA : " + doc.data());
    var theData = doc.data()['HOURLY'];
    console.log("The Data", theData);
    return theData;
  } else {
      console.log("No such document!");
  }
  }).catch(function(error) {
  console.log("Error getting document:", error);
 });
}

function getYesterdaysDate() {
    var date = new Date();
    date.setDate(date.getDate()-1);
    console.log("Yesterday date", String( date.getDate() + '' + (date.getMonth()+1) + '' + date.getFullYear()));
    return date.getDate() + '' + (date.getMonth()+1) + '' + date.getFullYear();
}

function getTodaysDate(){
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1; //January is 0 otherwise
  var yyyy = today.getFullYear();

  if (dd < 10) {
    dd = '0' + dd;
  }

  if (mm < 10) {
    mm = '0' + mm;
  }

  today = dd +  mm + yyyy;
  return today;
}

function getDailyStats(){
  //  console.log(getTodaysDate());
}

function getTotalToday(){
  var db = firebaseApp.firestore();
  const comments = [];
  //This will get the most popular time
  console.log("The Data", String(getTodaysDate()));
  var docRef = db.collection("PROCESSED").doc(String(getTodaysDate()));
  docRef.get().then(function(doc) {
  if (doc.exists) {
    var theData = doc.data()['TOTALTODAY'];
    console.log("The Data", theData);
    return theData;
  } else {
      console.log("No such document!");
  }
  }).catch(function(error) {
  console.log("Error getting document:", error);
 });
 console.log("getTotalToday is: ", "Finished");
}

function getTotalYesturday(){

}
