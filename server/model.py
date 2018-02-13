"""File: model.py
Flask-SQLAlchemy model declaration.
"""

from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
from app import app
import hashlib, binascii


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
    """
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False, unique=True)
    pw_hash = db.Column(db.String, nullable=False)

    def __init__(self, **kwargs):
        self.pw_hash = self.hash_password(kwargs['password'])
        self.name = kwargs['name']

    def hash_password(self, password):
        h = hashlib.pbkdf2_hmac('sha256', bytearray(password, 'utf8'), app.config['SECRET_KEY'], 100000)
        return binascii.hexlify(h)
    
    def verify_password(self, password):
        if self.pw_hash == self.hash_password(password):
            return True
        else:
            return False

    def __repr__(self):
        return '<User {}>'.format(self.name)
