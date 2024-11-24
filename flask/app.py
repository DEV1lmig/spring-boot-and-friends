from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

@app.route('/buscar', methods=['GET'])
def buscar():
    cedula = request.args.get('cedula')
    primer_nombre = request.args.get('primer_nombre')
    nombre_completo = request.args.get('nombre_completo')

    # Ejemplo de respuesta
    response = {
        "status": "success",
        "data": {
            "cedula": cedula,
            "primer_nombre": primer_nombre,
            "nombre_completo": nombre_completo
        }
    }

    return jsonify(response)

@app.route('/')
def home():
    return jsonify({"status": "Flask service is running"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
