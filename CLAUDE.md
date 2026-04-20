# CLAUDE.md

## Process Logging

After every user input, append an entry to `BEN_PROCESS.md` using this exact table row format:

```
| <ISO 8601 UTC timestamp with minute precision, e.g. 2026-04-20T23:30Z> | `<verbatim user input>` |
```

Use `date -u +"%Y-%m-%dT%H:%MZ"` to get the current timestamp. Append to the existing table — do not rewrite the file. Do this before taking any other action in response to the input.
