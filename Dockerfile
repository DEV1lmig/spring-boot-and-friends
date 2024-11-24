FROM postgres:13

RUN apt-get update && apt-get install -y python3 python3-psycopg2

COPY scripts/nacional.csv /docker-entrypoint-initdb.d/nacional.csv
COPY scripts/load_data.py /docker-entrypoint-initdb.d/load_data.py

ENTRYPOINT ["sh", "-c", "exec postgres & sleep 10 & python3 /docker-entrypoint-initdb.d/load_data.py"]
