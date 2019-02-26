
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


var client = new Keen({
  projectId: '5337e28273f4bb4499000000',
  readKey: '8827959317a6a01257bbadf16c12eff4bc61a170863ca1dadf9b3718f56bece1ced94552c6f6fcda073de70bf860c622ed5937fcca82d57cff93b432803faed4108d2bca310ca9922d5ef6ea9381267a5bd6fd35895caec69a7e414349257ef43a29ebb764677040d4a80853e11b8a3f'
});

var geoProject = new Keen({
  projectId: '53eab6e12481962467000000',
  readKey: 'd1b97982ce67ad4b411af30e53dd75be6cf610213c35f3bd3dd2ef62eaeac14632164890413e2cc2df2e489da88e87430af43628b0c9e0b2870d0a70580d5f5fe8d9ba2a6d56f9448a3b6f62a5e6cdd1be435c227253fbe3fab27beb0d14f91b710d9a6e657ecf47775281abc17ec455'
});

function updateDisplay() {
  $('#day').html(date_time('day').toString());
//  $('#sameday').html(getTotalToday().toString());
//  $('#getTotalYesturday').html(getTotalYesturday().toString());
}

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


function date_time(id)
{
        date = new Date;
        year = date.getFullYear();
        month = date.getMonth();
        months = new Array('January', 'February', 'March', 'April', 'May', 'June', 'Jully', 'August', 'September', 'October', 'November', 'December');
        d = date.getDate();
        day = date.getDay();
        days = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday');
        h = date.getHours();
        if(h<10)
        {
                h = "0"+h;
        }
        m = date.getMinutes();
        if(m<10)
        {
                m = "0"+m;
        }
        s = date.getSeconds();
        if(s<10)
        {
                s = "0"+s;
        }
        result = ''+days[day]+' '+months[month]+' '+d+' '+year+' '+h+':'+m+':'+s;
        document.getElementById(id).innerHTML = result;
        setTimeout('date_time("'+id+'");','1000');
        return true;
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



Keen.ready(function(){

  var tabVisitors = document.getElementById('tab-visitors');
  var tabBrowsers = document.getElementById('tab-browsers');
  var tabGeography = document.getElementById('tab-geography');
  var activeRequest;

  var chart_visitors = new Keen.Dataviz()
    .el('#visitors')
    .height(300)
    .title('Monthly Visits')
    .type('area');

  var chart_browsers = new Keen.Dataviz()
    .el('#browser')
    .type('line')
    .height(300);

  var chart_geographies = new Keen.Dataviz()
    .el('#geography')
    .type('horizontal-bar')
    .height(300);

  tabVisitors.onclick = selectVisitorTab;
  tabBrowsers.onclick = selectBrowserTab;
  tabGeography.onclick = selectGeographyTab;

  selectVisitorTab();
  updateDisplay();


  function selectVisitorTab(e) {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    chart_visitors.prepare();
    if (activeRequest) {
      activeRequest.cancel();
    }
    activeRequest = renderVisitorTab(chart_visitors);
  }

  function selectBrowserTab(e) {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    chart_browsers.prepare();
    if (activeRequest) {
      activeRequest.cancel();
    }
    activeRequest = renderBrowserTab(chart_browsers);
  }

  function selectGeographyTab(e) {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    chart_geographies.prepare();
    if (activeRequest) {
      activeRequest.cancel();
    }
    activeRequest = renderGeographyTab(chart_geographies);
  }

  function renderVisitorTab(chart) {
    return geoProject
      .query('count', {
        event_collection: 'activations',
        interval: 'monthly',
        timeframe: {
          start: '2014-01-01',
          end: '2014-12-01'
        }
      })
      .then(function(res) {
        chart
          .data(res)
          .render();
      })
      .catch(function(err) {
        chart
          .message(err.message);
      });
  }

  function renderBrowserTab(chart) {
    return geoProject
      .query('count', {
        event_collection: 'activations',
        group_by: 'device_model_name',
        interval: 'monthly',
        timeframe: {
          start: '2014-01-01',
          end: '2014-12-01'
        }
      })
      .then(function(res) {
        chart
          .data(res)
          .render();
      })
      .catch(function(err) {
        chart
          .message(err.message);
      });
  }

  function renderGeographyTab(chart) {
    return client
      .query('count', {
        event_collection: 'visit',
        group_by: 'visitor.geo.province',
        // interval: 'monthly',
        timeframe: {
          start: '2014-01-01',
          end: '2014-12-01'
        }
      })
      .then(function(res) {
        chart
          .data(res)
          .labelMapping({
            'New Jersey' : 'NJ',
            'Virginia' : 'VA',
            'California': 'CA',
            'Washington': 'WA',
            'Utah': 'UT',
            'Oregon': 'OR',
            'null': 'Other'
          })
          .sortGroups('desc')
          .render();
      })
      .catch(function(err) {
        chart
          .message(err.message);
      });
  }




  // ----------------------------------------
  // New Activations
  // ----------------------------------------

  $('.users').knob({
    angleArc: 250,
    angleOffset: -125,
    readOnly: true,
    min: 0,
    max: 500,
    fgColor: '#00bbde',
    height: 290,
    width: '95%'
  });

  geoProject
    .query('count_unique', {
      event_collection: 'activations',
      target_property: 'user.id'
    })
    .then(function(res) {
      $('.users').val(res.result).trigger('change');
    })
    .catch(function(err) {
      alert('An error occurred fetching New Activations metric');
    });


  // ----------------------------------------
  // Errors Detected
  // ----------------------------------------

  $('.errors').knob({
    angleArc:250,
    angleOffset:-125,
    readOnly:true,
    min:0,
    max:100,
    fgColor: '#fe6672',
    height: 290,
    width: '95%'
  });

  geoProject
    .query('count', {
      event_collection: 'user_action',
      filters: [
        {
          property_name: 'error_detected',
          operator: 'eq',
          property_value: true
        }
      ]
    })
    .then(function(res) {
      $('.errors').val(res.result).trigger('change');
    })
    .catch(function(err) {
      alert('An error occurred fetching Device Crashes metric');
    });


  // ----------------------------------------
  // Funnel
  // ----------------------------------------
  // var funnel = new Keen.Query('funnel', {
  //   steps: [
  //     {
  //        event_collection: 'purchases',
  //        actor_property: 'user.id'
  //     },
  //     {
  //       event_collection: 'activations',
  //       actor_property: 'user.id'
  //     },
  //     {
  //       event_collection: 'status_update',
  //       actor_property: 'user.id'
  //     },
  //     {
  //       event_collection: 'user_action',
  //       actor_property: 'user.id',
  //       filters: [] // where property 'total_sessions' == 2
  //     },
  //     {
  //       event_collection: 'user_action',
  //       actor_property: 'user.id',
  //       filters: [] // where property 'action' equals 'invited friend'
  //     }
  //   ]
  // });

  /*  This funnel is built from mock data */
  var sample_funnel = new Keen.Dataviz()
    .el('#chart-05')
    .colors(['#00cfbb'])
    .data({ result: [ 3250, 3000, 2432, 1504, 321 ] })
    .height(340)
    .type('bar')
    .labels(['Purchased Device', 'Activated Device', 'First Session', 'Second Session', 'Invited Friend'])
    .title(null)
    .render();

  // ----------------------------------------
  // Mapbox - Active Users
  // ----------------------------------------
  var tframe = {
    start: '2014-01-01',
    end: '2014-02-01'
  };

  var DEFAULTS = {
    coordinates: {
      lat: 37.77350,
      lng: -122.41104
    },
    zoom: 11
  };

  var initialize,
      map,
      markerStart = DEFAULTS.coordinates;

  var activeMapData,
      heat;

  function setActiveButton(button) {
    var classButtonNormal = 'btn btn-default';
    var classButtonSelected = 'btn btn-primary';

    switch (button) {
    default:
    case '7days':
      document.getElementById('7days').className = classButtonSelected;
      document.getElementById('14days').className = classButtonNormal;
      document.getElementById('28days').className = classButtonNormal;
      break;
    case '14days':
      document.getElementById('7days').className = classButtonNormal;
      document.getElementById('14days').className = classButtonSelected;
      document.getElementById('28days').className = classButtonNormal;
      break;
    case '28days':
      document.getElementById('7days').className = classButtonNormal;
      document.getElementById('14days').className = classButtonNormal;
      document.getElementById('28days').className = classButtonSelected;
      break;
    }
  }

  initialize = function() {
    setActiveButton('7days');

    L.mapbox.accessToken = 'pk.eyJ1Ijoia2Vlbi1pbyIsImEiOiIza0xnNXBZIn0.PgzKlxBmYkOq6jBGErpqOg';
    map = L.mapbox.map('map', 'keen-io.kae20cg0', {
      attributionControl: true,
      center: [markerStart.lat, markerStart.lng],
      zoom: DEFAULTS.zoom
    });

    heat = L.heatLayer([], { maxZoom: 14 });

    activeMapData = L.layerGroup().addTo(map);

    map.attributionControl.addAttribution('<a href=\'https://keen.io/\'>Custom Analytics by Keen IO</a>');

    var geoFilter = [];
    geoFilter.push({
      property_name : 'keen.location.coordinates',
      operator : 'within',
      property_value: {
        coordinates: [ -122.41104, 37.77350 ],
        max_distance_miles: 10
      }
    });

    var scoped_events = new Keen.Query('select_unique', {
      event_collection: 'user_action',
      target_property: 'keen.location.coordinates',
      timeframe: tframe,
      filters: geoFilter
    });

    function runQuery(){
      geoProject.run(scoped_events, function(err, res) {
        // console.log('events', res);
        activeMapData.clearLayers();

        Keen.utils.each(res.result, function(coord, index){
          var em = L.marker(new L.LatLng(coord[1], coord[0]), {
            icon: L.mapbox.marker.icon()
          }).addTo(activeMapData);
        });

        activeMapData.eachLayer(function(l) {
            heat.addTo(map).addLatLng(l.getLatLng());
        });
        activeMapData.clearLayers();
      });
    }


    var newgeoFilter = [];
    function resize(geo){

      geo = [];

      heat.setLatLngs([]);

      var center = map.getCenter();
      var zoom = map.getZoom();

      z = zoom-1;
      if (zoom === 0){
        radius = false;
      }
      else {
        radius = 10000/Math.pow(2,z);
      }
      // console.log(center, radius);



      geo.push({
        property_name : 'keen.location.coordinates',
        operator : 'within',
        property_value: {
          coordinates: [ center.lng, center.lat ],
          max_distance_miles: radius
        }

      });
      return geo;
    }


    map.on('zoomend', function(e) {
      newgeoFilter = resize(newgeoFilter);
      scoped_events.set({ filters: newgeoFilter });
      runQuery();
    });
    map.on('dragend', function(e) {
      newgeoFilter = resize(newgeoFilter);
      scoped_events.set({ filters: newgeoFilter });
      runQuery();
    });



    document.getElementById('14days').addEventListener('click', function() {
      setActiveButton('14days');
      newgeoFilter = resize(newgeoFilter);
      scoped_events.set({ filters: newgeoFilter,
                          timeframe: {
                            start: '2014-01-01',
                            end: '2014-04-01'
                          }
                        });
      runQuery();
    });

    document.getElementById('28days').addEventListener('click', function() {
      setActiveButton('28days');
      newgeoFilter = resize(newgeoFilter);
      scoped_events.set({ filters: newgeoFilter,
                          timeframe: {
                            start: '2014-01-01',
                            end: '2014-12-01'
                          }
                        });
      runQuery();
    });

    document.getElementById('7days').addEventListener('click', function() {
      setActiveButton('7days');
      newgeoFilter = resize(newgeoFilter);
      scoped_events.set({ filters: newgeoFilter,
                          timeframe: {
                            start: '2014-01-01',
                            end: '2014-02-01'
                          }
                        });
      runQuery();
    });

  };




initialize();
});
