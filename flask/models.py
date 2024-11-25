from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy()

class Cne(db.Model):
    __tablename__ = 'cne'
    id = db.Column(db.BigInteger, primary_key=True)
    nacionalidad = db.Column(db.String(15), nullable=False)
    cedula = db.Column(db.String(15), nullable=False)
    primer_apellido = db.Column(db.String(50))
    segundo_apellido = db.Column(db.String(50))
    primer_nombre = db.Column(db.String(50))
    segundo_nombre = db.Column(db.String(50))
    centro = db.Column(db.String(50))
    nombre_completo = db.Column(db.String(125))
    sexo = db.Column(db.String(1))
    foto = db.Column(db.String(65536))
    huellas = db.Column(db.String(65536))

class CneFotos(db.Model):
    __tablename__ = 'cne_fotos'
    id = db.Column(db.BigInteger, primary_key=True)
    cedula = db.Column(db.String(15), nullable=False)
    foto = db.Column(db.String(65536), nullable=False)

class Usuario(db.Model):
    __tablename__ = 'usuarios'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    role = db.Column(db.String(80), nullable=False)

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)
