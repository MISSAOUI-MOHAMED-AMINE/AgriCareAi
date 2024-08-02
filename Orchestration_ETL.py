from airflow import DAG
from airflow.operators.python import PythonOperator
import datetime
import pandas as pd
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import time
import re 
from datetime import datetime, timedelta
from datetime import datetime
from airflow.providers.microsoft.mssql.operators.mssql import MsSqlOperator
import pyodbc



def scroll_to_quarter_page(driver):
    total_height = driver.execute_script("return document.body.scrollHeight")
    quarter_height = total_height // 4
    driver.execute_script(f"window.scrollTo(0, {quarter_height});")
    time.sleep(2)  

def get_sunrise_details(driver):
    sunrise=driver.find_elements(By.CLASS_NAME, "sunrise-sunset__times-value")[0].text
    sunset=driver.find_elements(By.CLASS_NAME, "sunrise-sunset__times-value")[1].text
    return sunrise,sunset


def get_forcasting_details(driver):
    date_day=[]
    temperature_max=[]
    temperature_min=[]
    description_day=[]
    rain=[]
    forcasting_list=driver.find_element(By.CLASS_NAME, "daily-list-body")
    forcasting_list_day=forcasting_list.find_elements(By.CLASS_NAME, "daily-list-item ")
    for i in forcasting_list_day[1:]:
        date_day.append(i.find_element(By.CLASS_NAME, "date").text)
        temperature_max.append(i.find_element(By.CLASS_NAME, "temp-hi").text)
        temperature_min.append(i.find_element(By.CLASS_NAME, "temp-lo").text)
        description_day.append(i.find_element(By.CLASS_NAME, "no-wrap").text)
        rain.append(i.find_element(By.CLASS_NAME, "precip").text)
    return date_day,temperature_max,temperature_min,description_day

def get_today_weather_details(driver):
    details_weather_today = driver.find_elements(By.CLASS_NAME, "daily-list-item ")[0]
    temp_max_today=details_weather_today.find_element(By.CLASS_NAME, "temp-hi").text
    temp_min_today=details_weather_today.find_element(By.CLASS_NAME, "temp-lo").text
    description_day=details_weather_today.find_element(By.CLASS_NAME, "no-wrap").text
    date_today=details_weather_today.find_element(By.CLASS_NAME, "date").text
    icon=details_weather_today.find_element(By.CLASS_NAME, "icon").get_attribute("src")
    href_value = details_weather_today.get_attribute("href")
    driver.get(href_value)
    div_element = driver.find_element(By.CLASS_NAME, "half-day-card-content")
    div_element1 = div_element.find_element(By.CLASS_NAME, "left")
    p_elements = div_element1.find_elements(By.TAG_NAME, "p")
    div_element_right= div_element.find_element(By.CLASS_NAME, "right")
    p_elements_right = div_element_right.find_elements(By.TAG_NAME, "p")
    return p_elements[1].text,p_elements[2].text,p_elements[3].text,p_elements_right[2].text,temp_max_today,temp_min_today,description_day,date_today,icon

def get_forcasting_hours(driver):
    link_images=[]
    hour=[]
    temps=[]
    precips=[]
    time.sleep(2)

    list_hours=driver.find_element(By.CLASS_NAME, "hourly-list__list")
    list_hours.find_element(By.XPATH,"/html/body/div/div[7]/div[1]/div[1]/div[2]/div/button[2]").click()
    time.sleep(4)
    hours=list_hours.find_elements(By.CLASS_NAME,"hourly-list__list__item-time")
    list_images=list_hours.find_elements(By.CLASS_NAME,"hourly-list__list__item-icon")
    temp=list_hours.find_elements(By.CLASS_NAME,"hourly-list__list__item-temp")
    precip=list_hours.find_elements(By.CLASS_NAME,"hourly-list__list__item-precip")
    for i in hours[1:6] :
        hour.append(i.text)
    for j in  list_images[1:6] :
        link_images.append(j.get_attribute("src"))
    for k in temp[1:6] :
        temps.append(k.text)
    for l in  precip[1:6] :
        precips.append(l.text)    
    return link_images,hour, temps, precips


def get_all_details(): 
    cities=["Monastir","Sfax","Mahdia"]
    all_temp_max_today=[]
    all_temp_min_today=[]
    all_description_day_=[]
    all_date_today_=[]
    all_icon=[]
    sunrises=[]
    sunsets=[]
    vents=[]
    all_Précipitations_possibles=[]
    all_Rafales_vent=[]
    all_Couverture_nuageuse=[]
    all_date_today=[]
    all_temperature_max=[]
    all_temperature_min=[]
    all_descirpiton_day=[]
    all_link_images=[]
    all_hour=[]
    all_temps=[]
    all_precips=[]
    options = Options()
    options.headless = True
    driver = webdriver.Chrome(options=options)
    for i in cities:
        driver.get("https://www.accuweather.com/")
        input_element = driver.find_element(By.CLASS_NAME, "search-input")
        input_element.send_keys(i)
        input_element.send_keys(Keys.RETURN)
        time.sleep(5)  
        sunrise,sunset=get_sunrise_details(driver)
        sunrises.append(sunrise)
        sunsets.append(sunset)
        link_images,hour, temps, precips=get_forcasting_hours(driver)
        all_link_images.append(link_images)
        all_hour.append(hour)
        all_temps.append(temps)
        all_precips.append(precips)

        date_day,temperature_max,temperature_min,description_day=get_forcasting_details(driver)
        all_date_today.append(date_day)
        all_temperature_max.append(temperature_max)
        all_temperature_min.append(temperature_min)
        all_descirpiton_day.append(description_day)
        vent,Précipitations_possibles,Rafales_vent,Couverture_nuageuse,temp_max_today,temp_min_today,description_day_,date_today,icon=get_today_weather_details(driver)
        vents.append(vent)
        all_Précipitations_possibles.append(Précipitations_possibles)
        all_Rafales_vent.append(Rafales_vent)
        all_Couverture_nuageuse.append(Couverture_nuageuse)
        all_temp_max_today.append(temp_max_today)
        all_temp_min_today.append(temp_min_today)
        all_description_day_.append(description_day_)
        all_date_today_.append(date_today)
        all_icon.append(icon)
    return  sunrises,sunsets, vents,all_Précipitations_possibles,all_Rafales_vent,all_Couverture_nuageuse, all_date_today,all_temperature_max,all_temperature_min,all_descirpiton_day,all_link_images,all_hour,all_temps,all_precips,all_temp_max_today,all_temp_min_today,all_description_day_,all_date_today_,all_icon
  
def transform_df_forcast_hours(**context):
    cities=["Monastir","Sfax","Mahdia"]
    all_hour=context['task_instance'].xcom_pull(key="all_hour", task_ids='Extract_data')
    all_temps=context['task_instance'].xcom_pull(key="all_temps", task_ids='Extract_data')
    all_precips=context['task_instance'].xcom_pull(key="all_precips", task_ids='Extract_data')
    all_link_images=context['task_instance'].xcom_pull(key="all_link_images", task_ids='Extract_data')

    all_hour = [item for sublist in all_hour for item in sublist]
    all_temps = [item for sublist in all_temps for item in sublist]
    all_precips = [item for sublist in all_precips for item in sublist]
    all_link_images = [item for sublist in all_link_images for item in sublist]
    forcast_hours_df= pd.DataFrame({
    'heures':  all_hour, 
    'temperatures':all_temps,
    'precepitations':all_precips,
    'icones' :all_link_images,
    })
    num_rows = len(forcast_hours_df) // len(cities)
    city_column = [city for city in cities for _ in range(num_rows)]
    forcast_hours_df.insert(0, 'Ville', city_column)
    context['ti'].xcom_push(key="forcast_hours_df", value=forcast_hours_df)

    return forcast_hours_df

def clean_dataframe_today_weather(df):
    columns_to_clean = ['Vent', 'Rafales de vent', 'Précipitations possibles', 'Couverture_nuageuse']
    
    for column in columns_to_clean:
        df[column] = df[column].apply(lambda x: x.split('\n')[-1] if isinstance(x, str) else x)
    
    return df

def convert_date_today(date_str, year):
    day_month = date_str.split('\n')[-1]
    day, month = day_month.split('/')
    real_date = datetime(year, int(month), int(day))
    return real_date.strftime('%m/%d/%Y')


def transform_today_weather_df(**context):    
    cities=["Monastir","Sfax","Mahdia"]
    sunrises=context['task_instance'].xcom_pull(key="sunrises", task_ids='Extract_data')
    sunsets=context['task_instance'].xcom_pull(key="sunsets", task_ids='Extract_data')
    vents=context['task_instance'].xcom_pull(key="vents", task_ids='Extract_data')
    all_Précipitations_possibles=context['task_instance'].xcom_pull(key="all_Précipitations_possibles", task_ids='Extract_data')
    all_Couverture_nuageuse=context['task_instance'].xcom_pull(key="all_Couverture_nuageuse", task_ids='Extract_data')
    all_Rafales_vent=context['task_instance'].xcom_pull(key="all_Rafales_vent", task_ids='Extract_data')
    all_temp_max_today=context['task_instance'].xcom_pull(key="all_temp_max_today", task_ids='Extract_data')
    all_temp_min_today=context['task_instance'].xcom_pull(key="all_temp_min_today", task_ids='Extract_data')
    all_description_day_=context['task_instance'].xcom_pull(key="all_description_day_", task_ids='Extract_data')
    all_date_today_=context['task_instance'].xcom_pull(key="all_date_today_", task_ids='Extract_data')
    all_icon=context['task_instance'].xcom_pull(key="all_icon", task_ids='Extract_data')
    today_weather_df= pd.DataFrame({
    'Ville':  cities, 
    'date':all_date_today_,
    'Temperature max':all_temp_max_today,
    'Temperature min':all_temp_min_today,
    'description':all_description_day_,
    'etat':all_icon,
    'Lever':sunrises,
    'Coucher':sunsets,
    'Vent' :vents,
    'Rafales de vent':all_Précipitations_possibles,
    'Précipitations possibles' :all_Rafales_vent,
    'Couverture_nuageuse':all_Couverture_nuageuse

    })
    today_weather_df = today_weather_df=clean_dataframe_today_weather(today_weather_df)
    today_weather_df['date'] = today_weather_df['date'].apply(lambda x: convert_date_today(x, 2024))
    context['ti'].xcom_push(key="today_weather_df", value=today_weather_df)
    return today_weather_df


def clean_temperature(temp_str):
        return re.sub(r'[^0-9]', '', temp_str)

def convert_dates(df):
    def parse_date(date_str):
        day_str, date_part = date_str.split('\n')
        day_mapping = {
            'LUN.': 'Monday',
            'MAR.': 'Tuesday',
            'MER.': 'Wednesday',
            'JEU.': 'Thursday',
            'VEN.': 'Friday',
            'SAM.': 'Saturday',
            'DIM.': 'Sunday'
        }
        
        # Convert date_part to the desired format
        date = datetime.strptime(date_part, '%d/%m').replace(year=2024)
        return date.strftime('%m/%d/%Y')

    df['dates'] = df['dates'].apply(parse_date)
    return df







def transform_forecasting_weather_df(**context):
    cities=["Monastir","Sfax","Mahdia"]
    all_temperature_max=list(context['task_instance'].xcom_pull(key="all_temperature_max", task_ids='Extract_data'))
    print("ggg",all_temperature_max)
    all_date_today=list(context['task_instance'].xcom_pull(key="all_date_today", task_ids='Extract_data'))
    all_temperature_min=list(context['task_instance'].xcom_pull(key="all_temperature_min", task_ids='Extract_data'))
    all_descirpiton_day=list(context['task_instance'].xcom_pull(key="all_descirpiton_day", task_ids='Extract_data'))
    all_temperature_max_flat = [item for sublist in all_temperature_max for item in sublist]
    all_date_today_flat = [item for sublist in all_date_today for item in sublist]
    all_temperature_min_flat = [item for sublist in all_temperature_min for item in sublist]
    all_description_day_flat = [item for sublist in all_descirpiton_day for item in sublist]

    forecasting_weather_df = pd.DataFrame({
        "dates": all_date_today_flat,
        "temperature max": all_temperature_max_flat,
        "temperature min": all_temperature_min_flat,
        "descriptions": all_description_day_flat
    })
    forecasting_weather_df = convert_dates(forecasting_weather_df)
    forecasting_weather_df['temperature max'] = forecasting_weather_df['temperature max'].apply(clean_temperature)
    forecasting_weather_df['temperature min'] = forecasting_weather_df['temperature min'].apply(clean_temperature)
    num_rows = len(forecasting_weather_df) // len(cities)
    city_column = [city for city in cities for _ in range(num_rows)]
    forecasting_weather_df.insert(0, 'Ville', city_column)
    context['ti'].xcom_push(key="forecasting_weather_df", value=forecasting_weather_df)

    return forecasting_weather_df



def extraction_task(**context):
    sunrises,sunsets, vents,all_Précipitations_possibles,all_Rafales_vent,all_Couverture_nuageuse, all_date_today,all_temperature_max,all_temperature_min,all_descirpiton_day,all_link_images,all_hour,all_temps,all_precips,all_temp_max_today,all_temp_min_today,all_description_day_,all_date_today_,all_icon=get_all_details()
    context['ti'].xcom_push(key="sunrises", value=sunrises)
    context['ti'].xcom_push(key="sunsets", value=sunsets)
    context['ti'].xcom_push(key="vents", value=vents)
    context['ti'].xcom_push(key="all_Précipitations_possibles", value=all_Précipitations_possibles)
    context['ti'].xcom_push(key="all_Rafales_vent", value=all_Rafales_vent)
    context['ti'].xcom_push(key="all_Couverture_nuageuse", value=all_Couverture_nuageuse)
    context['ti'].xcom_push(key="all_date_today", value=all_date_today)
    context['ti'].xcom_push(key="all_temperature_max", value=all_temperature_max)
    context['ti'].xcom_push(key="all_temperature_min", value=all_temperature_min)
    context['ti'].xcom_push(key="all_descirpiton_day", value=all_descirpiton_day)
    context['ti'].xcom_push(key="all_link_images", value=all_link_images)
    context['ti'].xcom_push(key="all_hour", value=all_hour)
    context['ti'].xcom_push(key="all_temps", value=all_temps)
    context['ti'].xcom_push(key="all_precips", value=all_precips)
    context['ti'].xcom_push(key="all_temp_max_today", value=all_temp_max_today)
    context['ti'].xcom_push(key="all_description_day_", value=all_description_day_)
    context['ti'].xcom_push(key="all_temp_min_today", value=all_temp_min_today)
    context['ti'].xcom_push(key="all_date_today_", value=all_date_today_)
    context['ti'].xcom_push(key="all_icon", value=all_icon)


def establish_connection():
    driver = '{ODBC Driver 17 for SQL Server}'
    server = ''
    database = ''
    username = ''
    password = ''
    connection_string = f'DRIVER={driver};SERVER={server};PORT=1433;DATABASE={database};UID={username};PWD={password}'
    conn = pyodbc.connect(connection_string)
    cursor = conn.cursor()
    return conn, cursor

def insert_forecasting_weather_data(conn, cursor, forecasting_weather_df):
    for i, row in forecasting_weather_df.iterrows():
        sql = '''
        INSERT INTO weather_df (ville, dates, temp_max, temp_min, descriptions)
        VALUES (?, ?, ?, ?, ?)
        '''
        cursor.execute(sql, row['Ville'], row['dates'], row['temperature max'], row['temperature min'], row['descriptions'])
    conn.commit()

def insert_today_weather_data(conn, cursor, today_weather_df):
    for i, row in today_weather_df.iterrows():
        sql = '''
        INSERT INTO today_df (ville, dates, temperature_max, temperature_min, description, etat, lever, coucher, vent, rafales_de_vent, precipitations_possibles, couverture_nuageuse)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        '''
        cursor.execute(sql, (
            row['Ville'], row['date'], row['Temperature max'], row['Temperature min'], row['description'], 
            row['etat'], row['Lever'], row['Coucher'], row['Vent'], row['Rafales de vent'],
            row['Précipitations possibles'], row['Couverture_nuageuse']
        ))
    conn.commit()

def insert_forcast_hours_data(conn, cursor, forcast_hours_df):
    for i, row in forcast_hours_df.iterrows():
        sql = '''
        INSERT INTO heure_df (ville, heures, temperatures, precipitations, icones)
        VALUES (?, ?, ?, ?, ?)
        '''
        cursor.execute(sql, row['Ville'], row['heures'], row['temperatures'], row['precepitations'], row['icones'])
    conn.commit()

conn, cursor = establish_connection()

def loading___data(**context):

    df1 = context['task_instance'].xcom_pull(key="forecasting_weather_df", task_ids='transform_forecasting_weather')
    df2 = context['task_instance'].xcom_pull(key="forcast_hours_df", task_ids='transform__forcast_hours')
    df3 = context['task_instance'].xcom_pull(key="today_weather_df", task_ids='transform_today_weather')


    conn, cursor = establish_connection()


# Insert data into respective tables
    insert_today_weather_data(conn, cursor, df3)
    insert_forecasting_weather_data(conn, cursor, df1)
    insert_forcast_hours_data(conn, cursor, df2)




default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
   'start_date': datetime(2024, 7, 29),
    'email': ['ghaith.krifa@isgs.u-sousse.tn'],
    'email_on_failure': True,
    'email_on_retry': True,
   'retries': 1,
   'retry_delay': timedelta(minutes=5),
}

with DAG('Agrisense_ETL',
         default_args=default_args,
         schedule_interval='0 20 * * *',
         catchup=False
         ) as dag:

    Extract_data = PythonOperator(
        task_id='Extract_data',
        python_callable=extraction_task,
        provide_context=True,
    )
    transform_forecasting_weather = PythonOperator(
        task_id='transform_forecasting_weather',
        python_callable=transform_forecasting_weather_df,
        provide_context=True,
    )
    transform__forcast_hours = PythonOperator(
        task_id='transform__forcast_hours',
        python_callable=transform_df_forcast_hours,
        provide_context=True,
    )
    transform_today_weather = PythonOperator(
        task_id='transform_today_weather',
        python_callable=transform_today_weather_df,
        provide_context=True,
    )
    loading_data = PythonOperator(
        task_id='loading_data',
        python_callable=loading___data,
        provide_context=True,
    )
    truncate_table_hour = MsSqlOperator(
        task_id='truncate_table_hour',
        mssql_conn_id='agrisense_krifa',
        sql="TRUNCATE TABLE  heure_df;",
    )
    truncate_table_weather =  MsSqlOperator(
        task_id='truncate_table_weather',
        mssql_conn_id='agrisense_krifa',
        sql="TRUNCATE TABLE  weather_df;",
    )

    truncate_table_today = MsSqlOperator(
        task_id='truncate_table_today',
        mssql_conn_id='agrisense_krifa',
        sql="TRUNCATE TABLE  today_df;",
    )
    Extract_data >>  truncate_table_today >> truncate_table_weather >>  truncate_table_hour>> [transform_forecasting_weather,transform__forcast_hours,transform_today_weather]  >> loading_data
