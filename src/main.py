from fastapi import FastAPI
from src.routes import router

app = FastAPI(title="Sample CI Project", version="1.0.0")
app.include_router(router)

@app.get("/health")
def health():
    return {"status": "ok"}
