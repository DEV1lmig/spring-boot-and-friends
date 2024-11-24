from flask import Flask, request, jsonify
from models import db, Cne

app = Flask(__name__)
app.config.from_object('config')
db.init_app(app)

@app.route('/buscar', methods=['GET'])
def buscar():
    query_params = request.args
    cedula = query_params.get('cedula')
    primer_nombre = query_params.get('primer_nombre')
    nombre_completo = query_params.get('nombre_completo')
    query = Cne.query
    if cedula:
        query = query.filter(Cne.cedula == cedula)
    if primer_nombre:
        query = query.filter(Cne.primer_nombre == primer_nombre)
    if nombre_completo:
        query = query.filter(Cne.nombre_completo.ilike(f"%{nombre_completo}%"))
    results = query.all()
    return jsonify([cne.to_dict() for cne in results])

if __name__ == '__main__':
    app.run(debug=True)
