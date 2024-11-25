from flask import Flask, request, jsonify
from flask_cors import CORS
from models import db, Cne, Usuario
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from config import Config

app = Flask(__name__)
app.config.from_object(Config)
jwt = JWTManager(app)
db.init_app(app)
CORS(app)

@app.route('/auth/register', methods=['POST'])
def register():
    username = request.json.get('username')
    password = request.json.get('password')
    role = request.json.get('role', 'user')  # Rol por defecto 'user'

    if not username or not password:
        return jsonify({'error': 'Username y password son requeridos'}), 400

    if Usuario.query.filter_by(username=username).first():
        return jsonify({'error': 'El usuario ya existe'}), 400

    new_user = Usuario(username=username, role=role)
    new_user.set_password(password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({'message': 'Usuario registrado exitosamente'}), 201

@app.route('/auth/login', methods=['POST'])
def login():
    username = request.json.get('username')
    password = request.json.get('password')

    user = Usuario.query.filter_by(username=username).first()
    if user and user.check_password(password):
        access_token = create_access_token(identity=str(user.id))
        return jsonify({
            'token': access_token,
            'role': user.role
        })
    return jsonify({'error': 'Credenciales inválidas'}), 401

@app.route('/validate-token', methods=['POST', 'GET', 'PUT', 'DELETE'])
@jwt_required()
def validate_token():
    try:
        user_id = get_jwt_identity()
        user = Usuario.query.get(user_id)
        return jsonify({
            'valid': True,
            'role': user.role
        })
    except:
        return jsonify({'valid': False}), 401

@app.route('/buscar', methods=['GET'])
@jwt_required()
def buscar():
    cedula = request.args.get('cedula')
    primer_nombre = request.args.get('primer_nombre')
    nombre_completo = request.args.get('nombre_completo')

    query = Cne.query

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
