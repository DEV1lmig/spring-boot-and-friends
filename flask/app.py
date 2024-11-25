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

@app.route('/cne', methods=['GET'])
def get_all():
    cnes = Cne.query.all()
    return jsonify([{
        'id': cne.id,
        'nacionalidad': cne.nacionalidad,
        'cedula': cne.cedula,
        'primer_apellido': cne.primer_apellido,
        'segundo_apellido': cne.segundo_apellido,
        'primer_nombre': cne.primer_nombre,
        'segundo_nombre': cne.segundo_nombre,
        'centro': cne.centro,
        'nombre_completo': cne.nombre_completo,
        'sexo': cne.sexo,
        'foto': cne.foto,
        'huellas': cne.huellas
    } for cne in cnes])

@app.route('/cne/<int:id>', methods=['GET'])
def get_by_id(id):
    cne = Cne.query.get_or_404(id)
    return jsonify({
        'id': cne.id,
        'nacionalidad': cne.nacionalidad,
        'cedula': cne.cedula,
        'primer_apellido': cne.primer_apellido,
        'segundo_apellido': cne.segundo_apellido,
        'primer_nombre': cne.primer_nombre,
        'segundo_nombre': cne.segundo_nombre,
        'centro': cne.centro,
        'nombre_completo': cne.nombre_completo,
        'sexo': cne.sexo,
        'foto': cne.foto,
        'huellas': cne.huellas
    })
@app.route('/cne', methods=['POST'])
def create():
    data = request.get_json()
    cne = Cne(
        nacionalidad=data.get('nacionalidad'),
        cedula=data.get('cedula'),
        primer_apellido=data.get('primer_apellido'),
        segundo_apellido=data.get('segundo_apellido'),
        primer_nombre=data.get('primer_nombre'),
        segundo_nombre=data.get('segundo_nombre'),
        centro=data.get('centro'),
        nombre_completo=data.get('nombre_completo'),
        sexo=data.get('sexo'),
        foto=data.get('foto'),
        huellas=data.get('huellas')
    )
    db.session.add(cne)
    db.session.commit()
    return jsonify({
        'mensaje': 'CNE creado exitosamente',
        'id': cne.id
    }), 201

@app.route('/cne/<int:id>', methods=['PUT'])
def update(id):
    cne = Cne.query.get_or_404(id)
    data = request.get_json()

    cne.nacionalidad = data.get('nacionalidad', cne.nacionalidad)
    cne.cedula = data.get('cedula', cne.cedula)
    cne.primer_apellido = data.get('primer_apellido', cne.primer_apellido)
    cne.segundo_apellido = data.get('segundo_apellido', cne.segundo_apellido)
    cne.primer_nombre = data.get('primer_nombre', cne.primer_nombre)
    cne.segundo_nombre = data.get('segundo_nombre', cne.segundo_nombre)
    cne.centro = data.get('centro', cne.centro)
    cne.nombre_completo = data.get('nombre_completo', cne.nombre_completo)
    cne.sexo = data.get('sexo', cne.sexo)
    cne.foto = data.get('foto', cne.foto)
    cne.huellas = data.get('huellas', cne.huellas)

    db.session.commit()
    return jsonify({
        'mensaje': 'CNE actualizado exitosamente',
        'id': cne.id
    })

@app.route('/cne/<int:id>', methods=['DELETE'])
def delete(id):
    cne = Cne.query.get_or_404(id)
    db.session.delete(cne)
    db.session.commit()
    return jsonify({
        'mensaje': 'CNE eliminado exitosamente',
        'id': id
    })

@app.route('/fotos', methods=['GET'])
def get_all_fotos():
    fotos = CneFotos.query.all()
    return jsonify([{
        'id': foto.id,
        'cedula': foto.cedula,
        'foto': foto.foto
    } for foto in fotos])

@app.route('/fotos/<int:id>', methods=['GET'])
def get_foto_by_id(id):
    foto = CneFotos.query.get_or_404(id)
    return jsonify({
        'id': foto.id,
        'cedula': foto.cedula,
        'foto': foto.foto
    })

@app.route('/fotos', methods=['POST'])
def create_foto():
    data = request.get_json()
    foto = CneFotos(
        cedula=data.get('cedula'),
        foto=data.get('foto')
    )
    db.session.add(foto)
    db.session.commit()
    return jsonify({
        'mensaje': 'Foto creada exitosamente',
        'id': foto.id
    }), 201

@app.route('/fotos/<int:id>', methods=['PUT'])
def update_foto(id):
    foto = CneFotos.query.get_or_404(id)
    data = request.get_json()

    foto.cedula = data.get('cedula', foto.cedula)
    foto.foto = data.get('foto', foto.foto)

    db.session.commit()
    return jsonify({
        'mensaje': 'Foto actualizada exitosamente',
        'id': foto.id
    })

@app.route('/fotos/<int:id>', methods=['DELETE'])
def delete_foto(id):
    foto = CneFotos.query.get_or_404(id)
    db.session.delete(foto)
    db.session.commit()
    return jsonify({
        'mensaje': 'Foto eliminada exitosamente',
        'id': id
    })

@app.errorhandler(404)
def not_found(error):
    return jsonify({'error': 'Recurso no encontrado'}), 404

@app.route('/')
def home():
    return jsonify({"status": "Flask service is running"})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
