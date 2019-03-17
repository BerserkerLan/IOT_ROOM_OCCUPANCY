import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore
from google.cloud.firestore_v1beta1 import ArrayUnion
import threading
import time

cred = credentials.Certificate("firebase_key.json")
firebase_admin = firebase_admin.initialize_app(cred)
db = firestore.client()


def get_fire_pir_in():
	ref_pir_in = db.collection('PIR_IN')
	docs_pir_in = ref_pir_in.get()
	return docs_pir_in

def get_fire_pir_out():
	ref_pir_out = db.collection('PIR_OUT')
	docs_pir_out = ref_pir_out.get()
	return docs_pir_out

def get_D1():
	ref_d1 = db.collection('D1')
	docs_d1 = ref_d1.get()
	return docs_d1

def get_D2():
	ref_d2 = db.collection('D2')
	docs_d2 = ref_d2.get()
	return docs_d2

def get_averages():
	average_doc = db.collection('AUX').document('AVERAGES').get()
	return average_doc.to_dict()

def get_most_popular_time_of_today():
	popular_time = db.collection('AUX').document('MOST_POPULAR_TIME_OF_TODAY').get()
	return popular_time.to_dict()

#Get people that went into the room throughout the whole day
def get_number_of_people_in_today():
	date_now = datetime.datetime.now()
	sum = 0
	docs_pir_in = get_fire_pir_in()
	for doc in docs_pir_in:
		if (doc.id == date_now.strftime("%Y%m%d")):
			for key,value in doc.to_dict().items():
				sum += int(value)
	return sum

#Get people that went out of the room throughout the whole day
def get_number_of_people_out_today():
	date_now = datetime.datetime.now()
	sum = 0
	docs_pir_out = get_fire_pir_out()
	for doc in docs_pir_out:
		if(doc.id == date_now.strftime("%Y%m%d")):
			for key,value in doc.to_dict().items():
				sum += int(value)
	return sum

#Gets people that went in at the current hour
def get_number_of_people_in_current_hour():
	date_now = datetime.datetime.now()
	sum = 0
	time_now = date_now.strftime("%H%M%S")
	docs_pir_in = get_fire_pir_in()
	for doc in docs_pir_in:
		if (doc.id == date_now.strftime("%Y%m%d")):
			for key,value in doc.to_dict().items():
				if (key[:2] == time_now[:2]):
					sum += int(value)
	return sum
#Update TOTALTODAY for the day today
def update_people_total_in_today():
	date_now = datetime.datetime.now()
	people_in_today = get_number_of_people_in_today()
	print("In the function, number of people is : {}".format(people_in_today))
	data = {"TOTALTODAY" : people_in_today}
	db.collection('PROCESSED_IN').document(date_now.strftime("%Y%m%d")).set(data, merge=True)

def update_pir_things():
	distance1_map = get_D1()
	distance2_map = get_D2()
	for distance1 in distance1_map:
		distance2_map = get_D2()
		for distance2 in distance2_map:
			if (distance1.id == distance2.id): #Same date
				dist_date = distance1.id
				for key1, value1 in distance1.to_dict().items():
					time1 = key1
					for key2, value2 in distance2.to_dict().items():
						time2 = key2
						gap = int(time1) - int(time2)
						if (gap <=20000 and gap >= 0): #2000 ms gap should be good I think
							date_now = datetime.datetime.now()
							doc_name = date_now.strftime("%Y%m%d")
							time = date_now.strftime("%H%M%S")
							data = {time : 1}
							db.collection('PIR_IN').document(doc_name).set(data, merge=True)
							db.collection('D1').document(dist_date).update({key1 : firestore.DELETE_FIELD})
							db.collection('D2').document(dist_date).update({key2 : firestore.DELETE_FIELD})
						elif (gap >= -20000 and gap <= 0):
							print("Met second cond")
							date_now = datetime.datetime.now()
							doc_name = date_now.strftime("%Y%m%d")
							time = date_now.strftime("%H%M%S")
							data = {time : 1}
							db.collection('PIR_OUT').document(doc_name).set(data, merge=True)
							db.collection('D1').document(dist_date).update({key1 : firestore.DELETE_FIELD})
							db.collection('D2').document(dist_date).update({key2 : firestore.DELETE_FIELD})
#Update the Hourly field of today
def update_people_total_in_current_hour():
	date_now = datetime.datetime.now()
	people_in_current_hour = get_number_of_people_in_current_hour()
	data = {"HOURLY" : {(date_now.strftime("%H") + "00") : people_in_current_hour}}
	db.collection('PROCESSED_IN').document(date_now.strftime("%Y%m%d")).set(data, merge=True)


def update_people_quarter_in_current_hour():
	date_now = datetime.datetime.now()
	people_in_current_hour = get_number_of_people_in_current_hour()
	data = {"QUARTERLY" : {date_now.strftime("%H%M") : people_in_current_hour }}
	db.collection('PROCESSED_IN').document(date_now.strftime("%Y%m%d")).set(data, merge=True)
	print("In the function, number of people IN quarter is {}".format(people_in_current_hour))


#Gets people that went in at the current hour
def get_number_of_people_out_current_hour():
        date_now = datetime.datetime.now()
        sum = 0
        time_now = date_now.strftime("%H%M%S")
        docs_pir_out = get_fire_pir_out()
        for doc in docs_pir_out:
                if (doc.id == date_now.strftime("%Y%m%d")):
                        for key,value in doc.to_dict().items():
                                if (key[:2] == time_now[:2]):
                                        sum += int(value)
        return sum


def update_time_averages():
	date_now = datetime.datetime.now()
	people_in = db.collection('PROCESSED_IN').document(date_now.strftime("%Y%m%d")).get()
	sum_in = 0
	people_out = db.collection('PROCESSED_OUT').document(date_now.strftime("%Y%m%d")).get()
	for items in people_in.to_dict().items():
		print("ITEM : {}".format(items))
		if (items[0] == "QUARTERLY"):
			print("ITEMS 11 : {}".format(items[1]))
			for key, value in items[1].items():
				print("VAL : {}, {}".format(key, value))
				if (key[:2] == date_now.strftime("%H")):
					print("Updating current average for current time")
					sum_in += int(value)
	sum_out = 0
	for items in people_out.to_dict().items():
		if (items[0] == "QUARTERLY"):
			print("IEMS 1 : {}".format(items[1]))
			for key, value in items[1].items():
				print("HASAKEY : {}".format(value))
				if (key[:2] == date_now.strftime("%H")):
					sum_out += int(value)
	print("SUM IN {}".format(sum_in))
	print("SUM OUT {}".format(sum_out))
	sum = sum_in - sum_out
	if (sum < 0):
		sum = 0
	print("Sum is {}".format(sum))
	data = {(date_now.strftime("%H:") + "00") : sum}
	db.collection('AUX').document('TODAY').set(data, merge=True)

#Update TOTALTODAY for the day today
def update_people_total_out_today():
        date_now = datetime.datetime.now()
        people_out_today = get_number_of_people_out_today()
        data = {"TOTALTODAY" : people_out_today}
        db.collection('PROCESSED_OUT').document(date_now.strftime("%Y%m%d")).set(data, merge=True)


#Update the Hourly field of today
def update_people_total_out_current_hour():
	date_now = datetime.datetime.now()
	people_out_current_hour = get_number_of_people_out_current_hour()
	data = {"HOURLY" : {(date_now.strftime("%H") + "00") : people_out_current_hour}}
	db.collection('PROCESSED_OUT').document(date_now.strftime("%Y%m%d")).set(data, merge=True)
#Update the quarter of people, only call this function every 15 minutes
def update_people_quarter_out_current_hour():
        date_now = datetime.datetime.now()
        people_out_current_hour = get_number_of_people_out_current_hour()
        data = {"QUARTERLY" : {date_now.strftime("%H%M") : people_out_current_hour }}
        db.collection('PROCESSED_OUT').document(date_now.strftime("%Y%m%d")).set(data, merge=True)

def get_current_occupancy():
	current_people = (get_number_of_people_in_today()) - (get_number_of_people_out_today())
	return current_people
def update_todays_averages():
	day_today = datetime.datetime.now().strftime("%A")
	print("Today is {}".format(day_today))
	averages = get_averages()
	average_in_db = averages[day_today]
	new_average = (int(average_in_db) + int(get_number_of_people_in_today()))/2
	data = {day_today : new_average}
	db.collection('AUX').document('AVERAGES').update(data)

def update_current_occupancy():
	current_occupancy = get_current_occupancy()
	data = {'CURRENT_OCCUPANCY' : current_occupancy}
	db.collection('AUX').document('TODAY').update(data)

def update_most_popular_time():
	current_occupancy = get_current_occupancy()
	most_popular_time = get_most_popular_time_of_today()
	if (current_occupancy > int(list(most_popular_time.values())[0])):
		time_now = datetime.datetime.now().strftime("%H%M%S")
		data = {time_now : current_occupancy}
		db.collection('AUX').document('MOST_POPULAR_TIME_OF_TODAY').set(data, merge=True)

def update_people_in_today():
	people_today = get_number_of_people_in_today()
	data = {'NUMBER_OF_PEOPLE_IN_TODAY' : people_today}
	db.collection('AUX').document('TODAY').update(data)

def update_people_out_today():
	people_today = get_number_of_people_out_today()
	data = {'NUMBER_OF_PEOPLE_OUT_TODAY' : people_today}
	db.collection('AUX').document('TODAY').update(data)

quarterly_time_list = ['00','15','30','45']

def update_every_1_mins():
	while True:
		print('Running the minute thread......')
		date_now_minute = datetime.datetime.now().strftime("%M")
		update_pir_things()
		if (date_now_minute in quarterly_time_list):
			print("Updating the value in db")
			update_people_quarter_in_current_hour()
			update_people_quarter_out_current_hour()
		update_todays_averages()
		update_current_occupancy()
		update_people_in_today()
		update_people_out_today()
		update_most_popular_time()
		time.sleep(5)


def update_every_10_seconds():
	while True:
		update_pir_things()
		time.sleep(5)

quarterly_thread = threading.Thread(target=update_every_1_mins, args=[]) #Try to do quarterly in a seperate thread

#secondly_thread = threading.Thread(target=update_every_10_seconds, args=[])
#secondly_thread.start()




while True:
	print('Running main loop......')
	update_people_total_in_today()
	update_people_total_in_current_hour()
	update_people_total_out_today()
	update_people_total_out_current_hour()
	update_time_averages()
	quarterly_thread.start()
	time.sleep(60*15)
