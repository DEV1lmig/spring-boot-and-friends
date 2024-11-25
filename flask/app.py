from flask import Flask, request, jsonify
from flask_cors import CORS
from models import db, Cne, CneFotos
from config import Config

app = Flask(__name__)
app.config.from_object(Config)
db.init_app(app)
CORS(app)

@app.route('/buscar', methods=['GET'])
def buscar():
    cedula = request.args.get('cedula')
    primer_nombre = request.args.get('primer_nombre')
    nombre_completo = request.args.get('nombre_completo')

    # Construir la consulta base
    query = Cne.query

    # Aplicar filtros según los parámetros recibidos
    if cedula:
        query = query.filter(Cne.cedula == cedula)
    if primer_nombre:
        query = query.filter(Cne.primer_nombre == primer_nombre)
    if nombre_completo:
        query = query.filter(Cne.nombre_completo == nombre_completo)

    # Ejecutar la consulta
    resultado = query.first()

    if resultado:
        response = {
            "status": "success",
            "data": {
                "id": resultado.id,
                "nacionalidad": resultado.nacionalidad,
                "cedula": resultado.cedula,
                "primer_apellido": resultado.primer_apellido,
                "segundo_apellido": resultado.segundo_apellido,
                "primer_nombre": resultado.primer_nombre,
                "segundo_nombre": resultado.segundo_nombre,
                "centro": resultado.centro,
                "nombre_completo": resultado.nombre_completo,
                "sexo": resultado.sexo,
                "foto": resultado.foto,
                "huellas": resultado.huellas
            }
        }
    else:
        response = {
            "status": "error",
            "message": "No se encontraron resultados"
        }

    return jsonify(response)

@app.errorhandler(404)
def not_found(error):
    return jsonify({'error': 'Recurso no encontrado'}), 404

@app.route('/')
def home():
    return jsonify({"status": "Flask service is running"})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
