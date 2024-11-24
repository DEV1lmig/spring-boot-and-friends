from flask_sqlalchemy import SQLAlchemy

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
