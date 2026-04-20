# CLAUDE.md

## Process Logging

After every user input, append an entry to `BEN_PROCESS.md` using this exact table row format:

```
| <ISO 8601 UTC timestamp> | `<verbatim user input>` |
```

Append to the existing table — do not rewrite the file. Do this before taking any other action in response to the input.
