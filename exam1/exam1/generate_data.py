import csv
import random
from datetime import datetime, timedelta

NUM_RECORDS = 10000 
OUTPUT_FILE = 'raw_telemetry.csv'
START_TIME = datetime(2055, 10, 12, 8, 0, 0)

ROVERS = [
    {"id": "R-01", "type": "Tracked"}, {"id": "R-02", "type": "Hover"},
    {"id": "R-03", "type": "Walker"}, {"id": "R-04", "type": "Hover"},
    {"id": "R-05", "type": "Tracked"}
]
SENSORS = ["Temperature", "Radiation", "Atmosphere"]
STRING_ERRORS = ["N/A", "NULL", "NaN", "#VALUE!", "ERR", ""]

def get_normal_value(sensor_type):
    if sensor_type == "Temperature": return round(random.uniform(-80.0, -10.0), 1)
    elif sensor_type == "Radiation": return round(random.uniform(50.0, 300.0), 1)
    elif sensor_type == "Atmosphere": return round(random.uniform(90.0, 100.0), 1)

def generate_row(current_time):
    rover = random.choice(ROVERS)
    sensor = random.choice(SENSORS)
    value = get_normal_value(sensor)
    status = "OK"

    # 制造连续高辐射告警状态
    if random.random() < 0.05:
        status = "WARNING"
        if sensor == "Radiation": value = round(random.uniform(410.0, 500.0), 1) 
    
    # 制造脏数据/异常数据
    error_chance = random.random()
    if error_chance < 0.05:
        value = random.choice(STRING_ERRORS)
        status = "ERROR"
    elif error_chance < 0.10:
        if sensor == "Temperature": value = random.choice([-9999.0, 5000.0])
        elif sensor == "Radiation": value = random.choice([-100.0, 99999.0])
        status = "ERROR"
    elif error_chance < 0.15:
        status = "ERROR"
        if random.random() < 0.5: value = ""

    return [
        current_time.strftime("%Y-%m-%dT%H:%M:%SZ"),
        rover["id"], rover["type"], sensor, value, status
    ]

print(f"正在生成 {NUM_RECORDS} 条星际探测数据...")
with open(OUTPUT_FILE, mode='w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(["Timestamp", "RoverID", "RoverType", "SensorType", "Value", "Status"])
    current_time = START_TIME
    for i in range(NUM_RECORDS):
        writer.writerow(generate_row(current_time))
        current_time += timedelta(seconds=random.randint(1, 5))
print(f"生成完毕！文件已保存为: {OUTPUT_FILE}")