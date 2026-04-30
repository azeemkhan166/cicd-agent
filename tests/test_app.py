import pytest
from fastapi.testclient import TestClient
from src.main import app
from src.utils import add, greet

client = TestClient(app)

# ── Utils tests ──────────────────────────────────────────
def test_add_positive():
    assert add(2, 3) == 5

def test_add_negative():
    assert add(-1, -1) == -2

def test_add_zero():
    assert add(0, 0) == 0

def test_greet_normal():
    assert greet("Surendra") == "Hello, Surendra!"

def test_greet_empty_raises():
    with pytest.raises(ValueError):
        greet("")

# ── API tests ────────────────────────────────────────────
def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json() == {"status": "ok"}

def test_hello_default():
    r = client.get("/api/hello")
    assert r.status_code == 200
    assert "Hello" in r.json()["message"]

def test_hello_with_name():
    r = client.get("/api/hello?name=Surendra")
    assert r.status_code == 200
    assert r.json()["message"] == "Hello, Surendra!"

def test_add_api():
    r = client.get("/api/add?a=10&b=20")
    assert r.status_code == 200
    assert r.json()["result"] == 30
