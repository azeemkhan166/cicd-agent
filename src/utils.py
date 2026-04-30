def add(a: int, b: int) -> int:
    return a + b

def greet(name: str) -> str:
    if not name:
        raise ValueError("Name cannot be empty")
    return f"Hello, {name}!"
