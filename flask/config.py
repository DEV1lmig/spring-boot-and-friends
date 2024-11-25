import datetime

class Config:
    SQLALCHEMY_DATABASE_URI = 'postgresql://postgres:123456@postgres:5432/basedatos'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = 'Theminer79.'
    JWT_ACCESS_TOKEN_EXPIRES = datetime.timedelta(hours=1)
