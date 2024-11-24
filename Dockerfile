FROM postgres:13

RUN apt-get update && apt-get install -y python3 python3-psycopg2

RUN useradd -r -s /bin/false appuser

COPY nacional.csv /docker-entrypoint-initdb.d/nacional.csv
COPY scripts/load_data.py /docker-entrypoint-initdb.d/load_data.py

RUN chown -R appuser:appuser /docker-entrypoint-initdb.d/

USER appuser

ENTRYPOINT ["sh", "-c", "exec postgres & sleep 10 & python3 /docker-entrypoint-initdb.d/load_data.py"]
