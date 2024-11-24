FROM postgres:13

# Install Python
RUN apt-get update && apt-get install -y python3 python3-psycopg2

# Copy your scripts
COPY nacional.csv /docker-entrypoint-initdb.d/nacional.csv
COPY scripts/load_data.py /docker-entrypoint-initdb.d/load_data.py

# Set the entrypoint
ENTRYPOINT ["sh", "-c", "exec postgres & sleep 10 & python3 /docker-entrypoint-initdb.d/load_data.py"]
