from fastapi import APIRouter
from src.utils import add, greet

router = APIRouter(prefix="/api")

@router.get("/hello")
def hello(name: str = "World"):
    return {"message": greet(name)}

@router.get("/add")
def add_numbers(a: int, b: int):
    return {"result": add(a, b)}
