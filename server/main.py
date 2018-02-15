"""File: main.py
Contains routing decorators for Flask server.
Main entry point for the program.
"""

from flask import request
from app import app
from model import User, Weight, db
import json


@app.route('/deauth_token', methods=['POST'])
def deauth_token():
    """Deauthorise user token.
    Return success if token is invalidated.
    Return error otherwise.
    """
    if request.method == "POST":
        data = json.loads(request.data.decode('utf8'))
        print(data)

        # Check for correct request arguments
        for key in ['username']:
            if key not in data:
                return 'Invalid parameters.', 400

        name = data['username']
        
        # Determine if user exists
        user = User.query.filter_by(name=name).first()
        if user is None:
            print('No such user.')
            return 'No such user.', 401
        
        # Username exists, deauth the token
        user.deauthorise_token()
        return 'Auth token invalid.', 200

@app.route('/verify_token', methods=['POST'])
def verify_token():
    """Verify user auth token.
    Return success if token is still valid.
    Return error otherwise.
    """
    if request.method == "POST":
        data = json.loads(request.data.decode('utf8'))
        print(data)

        # Check for correct request arguments
        for key in ['username', 'password']:
            if key not in data:
                return 'Invalid parameters.', 400

        name = data['username']
        password = data['password']
        
        # Determine if user exists
        user = User.query.filter_by(name=name).first()
        if user is None:
            print('No such user.')
            return 'No such user.', 401
        
        # Username exists, verify the password
        if not user.verify_token(password):
            print('Invalid token.')
            return 'Invalid token.', 401

        # Username exists, token is verified
        return 'Auth token valid.', 200

@app.route('/add_record', methods=['POST'])
def update():
    """Add a record into the database.
    
    Request data is a bytearray consisting of a JSON string.
    JSON object contains two key:value pairs:
    "name" -- the user's name in the database
    "password" -- the user's password for the database
    "value" -- the value to add to the database

    Return success if record added to the database.
    Return relevant error if failed.
    """
    if request.method == "POST":
        data = json.loads(request.data.decode('utf8'))
        print(data)
        
        # Check for correct request arguments
        for key in ['value', 'username', 'password']:
            if key not in data:
                return 'Invalid parameters.', 400

        value = int(data['value'])
        name = data['username']
        password = data['password']
        
        # Determine if user exists
        user = User.query.filter_by(name=name).first()
        if user is None:
            print('No such user.')
            return 'No such user.', 401
        
        # Username exists, verify the token
        if not user.verify_token(password):
            print('Wrong password.')
            return 'Wrong password.', 401

        # Username exists, auth token is verified
        # Add item to the db
        try:
            Weight(user=user, value=value)
            db.session.commit()
        except:
            return 'Error', 500
        else:
            return 'Success', 204

@app.route('/create_user', methods=['POST'])
def create_user():
    """Create new user in the database.
    
    Request data is a bytearray consisting of a JSON string.
    JSON object contains two key:value pairs:
    "name" -- the user's name in the database
    "password" -- the user's password for the database
    "value" -- the value to add to the database

    Return success if user added to the database.
    Return relevant error if failed.
    """
    if request.method == "POST":
        data = json.loads(request.data.decode('utf8'))
        print(data)
        
        # Check for correct request arguments
        for key in ['username', 'password']:
            if key not in data:
                return 'Invalid parameters.', 400

        name = data['username']
        password = data['password']
        
        # Determine if user exists
        user = User.query.filter_by(name=name).first()
        if user is not None:
            return 'User already exists.', 401

        # User does not exist, create new user
        try:
            user = User(name=name, password=password)
            db.session.add(user)
            db.session.commit()
        except:
            return 'Error', 500
        else:
            return user.auth_token, 201

@app.route('/authenticate_user', methods=['POST'])
def authenticate_user():
    """Authenticate existing user with the server.
    
    Request data is a bytearray consisting of a JSON string.
    JSON object contains two key:value pairs:
    "name" -- the user's name in the database
    "password" -- the user's password for the database

    Return auth token if user is authenticated.
    Return relevant error if failed.
    """
    if request.method == "POST":
        data = json.loads(request.data.decode('utf8'))
        print(data)

        # Check for correct request arguments
        for key in ['username', 'password']:
            if key not in data:
                return 'Invalid parameters.', 400

        name = data['username']
        password = data['password']
        
        # Determine if user exists
        user = User.query.filter_by(name=name).first()
        if user is None:
            print('No such user.')
            return 'No such user.', 401
        
        # Username exists, verify the password
        if not user.verify_password(password):
            print('Wrong password.')
            return 'Wrong password.', 401

        # Username exists, password is verified
        # Return authentication token, update db entry
        user.generate_auth_token()
        return user.auth_token, 200


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)
