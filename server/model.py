"""File: model.py
Flask-SQLAlchemy model declaration.
"""

from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
from app import app
import hashlib, binascii
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
import datetime as dt


db = SQLAlchemy(app)


class Weight(db.Model):
    """Model definition for table of weight entries.

    One-to-many relationship between User and Weight.
    Each weight entry consists of:
    - Numerical id (primary key)
    - Weight value
    - Date of entry
    - User (foreign key)
    """
    id = db.Column(db.Integer, primary_key=True)
    value = db.Column(db.Float, nullable=False)
    date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'),
        nullable=False)
    user = db.relationship('User',
        backref=db.backref('weights', lazy=True))

    def __repr__(self):
        return '<Weight {}>'.format(self.value)


class User(db.Model):
    """Model definition for table of users.

    Each user has:
    - Numerical id (primary key)
    - Name (unique)
    - Password (salted and hashed)
    - Auth token
    """
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False, unique=True)
    pw_hash = db.Column(db.String, nullable=False)
    auth_token = db.Column(db.String, nullable=False)

    def __init__(self, **kwargs):
        self.pw_hash = self.hash_password(kwargs['password'])
        self.name = kwargs['name']
        self.generate_auth_token()

    def hash_password(self, password):
        h = hashlib.pbkdf2_hmac('sha256', bytearray(password, 'utf8'), app.config['SECRET_KEY'], 100000)
        return binascii.hexlify(h)
    
    def verify_password(self, password):
        if self.pw_hash == self.hash_password(password):
            return True
        else:
            return False

    def verify_token(self, token):
        s = Serializer(app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except:
            return False
        return True

    def deauthorise_token(self):
        self.auth_token = b''

    def generate_auth_token(self):
        exp = dt.timedelta(days=7).total_seconds()
        s = Serializer(app.config['SECRET_KEY'], exp)
        self.auth_token = s.dumps({'id':self.id})

    def __repr__(self):
        return '<User {}>'.format(self.name)
