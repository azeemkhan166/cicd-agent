# Sample CI Project

FastAPI project with full CI pipeline — Security Scan + Regression Tests.

## Project Structure
```
sample_project/
├── src/
│   ├── main.py        # FastAPI app
│   ├── routes.py      # API routes
│   └── utils.py       # Business logic
├── tests/
│   └── test_app.py    # pytest tests
├── .github/
│   ├── workflows/
│   │   └── ci.yml     # CI pipeline
│   └── owasp-suppressions.xml
├── requirements.txt
├── requirements-dev.txt
└── .gitignore
```

## Run Locally
```bash
pip install -r requirements.txt -r requirements-dev.txt
uvicorn src.main:app --reload
```

## Run Tests
```bash
pytest tests/ --cov=src
```

## CI Pipeline
- Stage 1: Gitleaks + OWASP + Trivy (parallel)
- Stage 2: pytest (only if Stage 1 passes)
- Final Gate: CI Passed (merge blocked if any fail)
