from flask import Flask, request, jsonify
from flask_cors import CORS
import tensorflow as tf
import numpy as np
from PIL import Image
import io
import os

app = Flask(__name__)
CORS(app)

class PlantDiseaseModel:
    def __init__(self, model_path='ultra_fast_plant_model.h5'):
        self.model = tf.keras.models.load_model(model_path)
        self.image_size = (64, 64)
        self.class_names = [
            "Pepper__bell__Bacterial_spot", "Pepper__bell__healthy",
            "Potato__Early_blight", "Potato__healthy", "Potato__Late_blight",
            "Tomato__Target_Spot", "Tomato__Tomato_mosaic_virus",
            "Tomato__Tomato_YellowLeaf_Curl_Virus", "Tomato_Bacterial_spot",
            "Tomato_Early_blight", "Tomato_healthy", "Tomato_Late_blight",
            "Tomato_Leaf_Mold", "Tomato_Septoria_leaf_spot",
            "Tomato_Spider_mites_Two_spotted_spider_mite"
        ]
    
    def predict_disease(self, image_file):
        image = Image.open(io.BytesIO(image_file))
        if image.mode != 'RGB':
            image = image.convert('RGB')
        image = image.resize(self.image_size)
        img_array = np.array(image) / 255.0
        img_array = np.expand_dims(img_array, axis=0)
        
        predictions = self.model.predict(img_array, verbose=0)
        predicted_class = np.argmax(predictions[0])
        confidence = np.max(predictions[0])
        
        top_3_indices = np.argsort(predictions[0])[-3:][::-1]
        top_3 = [(self.class_names[i], float(predictions[0][i])) for i in top_3_indices]
        
        return {
            'disease': self.class_names[predicted_class],
            'confidence': float(confidence),
            'top_predictions': top_3,
            'is_healthy': 'healthy' in self.class_names[predicted_class].lower(),
            # ADD COMPATIBILITY WITH EXISTING RESPONSE FORMAT
            'status': self.class_names[predicted_class],
            'message': f"Detected: {self.class_names[predicted_class]} with {confidence:.2%} confidence"
        }

plant_model = PlantDiseaseModel()

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy'})

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Handle both "image" and "file" field names for compatibility
        if 'image' in request.files:
            file = request.files['image']
        elif 'file' in request.files:
            file = request.files['file']
        else:
            return jsonify({'error': 'No image file uploaded'}), 400
        
        if file.filename == '':
            return jsonify({'error': 'No file selected'}), 400
        
        image_data = file.read()
        result = plant_model.predict_disease(image_data)
        return jsonify(result)
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# if __name__ == '__main__':
#     print("Plant Disease ML Server starting on port 5000...")
#     app.run(host='0.0.0.0', port=5000, debug=False)
import os

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    print(f"Plant Disease ML Server starting on port {port}...")
    app.run(host='0.0.0.0', port=port, debug=False)
