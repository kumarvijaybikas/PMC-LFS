package org.europepmc.funding.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ErrorDocumentationController {

    @Hidden
    @GetMapping("favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }

    @Hidden
    @GetMapping(value = {"/docs/errors", "/errors", "/errors/{type}"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getErrorDocs(@PathVariable(value = "type", required = false) String type) {
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>API Error Documentation - Europe PMC Literature Funding Service</title>
                    <style>
                        :root {
                            --primary: #2563eb;
                            --bg: #0f172a;
                            --card-bg: #1e293b;
                            --text: #f8fafc;
                            --muted: #94a3b8;
                            --border: #334155;
                            --badge-400: #f59e0b;
                            --badge-500: #ef4444;
                            --badge-502: #8b5cf6;
                        }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                            background-color: var(--bg);
                            color: var(--text);
                            margin: 0;
                            padding: 2rem;
                            line-height: 1.6;
                        }
                        .container {
                            max-width: 900px;
                            margin: 0 auto;
                        }
                        header {
                            border-bottom: 1px solid var(--border);
                            padding-bottom: 1.5rem;
                            margin-bottom: 2rem;
                        }
                        h1 { color: #60a5fa; margin-bottom: 0.5rem; }
                        p.subtitle { color: var(--muted); margin-top: 0; }
                        .error-card {
                            background: var(--card-bg);
                            border: 1px solid var(--border);
                            border-radius: 8px;
                            padding: 1.5rem;
                            margin-bottom: 1.5rem;
                            scroll-margin-top: 2rem;
                        }
                        .error-header {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-bottom: 1rem;
                        }
                        .error-title {
                            font-size: 1.25rem;
                            font-weight: 600;
                            color: #93c5fd;
                        }
                        .badge {
                            padding: 0.25rem 0.75rem;
                            border-radius: 9999px;
                            font-weight: bold;
                            font-size: 0.85rem;
                        }
                        .badge-400 { background: #78350f; color: #fde68a; }
                        .badge-502 { background: #4c1d95; color: #ddd6fe; }
                        .badge-500 { background: #7f1d1d; color: #fecaca; }
                        code {
                            background: #0f172a;
                            padding: 0.2rem 0.4rem;
                            border-radius: 4px;
                            color: #38bdf8;
                            font-family: monospace;
                        }
                        pre {
                            background: #0b1120;
                            padding: 1rem;
                            border-radius: 6px;
                            overflow-x: auto;
                            color: #a5f3fc;
                            font-family: monospace;
                        }
                        a.nav-back {
                            display: inline-block;
                            color: #38bdf8;
                            text-decoration: none;
                            margin-bottom: 1rem;
                        }
                        a.nav-back:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <a href="/swagger-ui.html" class="nav-back">&larr; Back to Swagger UI</a>
                        <header>
                            <h1>API Error Reference</h1>
                            <p class="subtitle">Comprehensive guide to Problem Details (RFC 7807) returned by this service.</p>
                        </header>

                        <div class="error-card" id="invalid-query">
                            <div class="error-header">
                                <span class="error-title"><code>invalid-query</code> - Invalid Search Parameters</span>
                                <span class="badge badge-400">HTTP 400</span>
                            </div>
                            <p><strong>Cause:</strong> The search query string was empty, blank, or the <code>limit</code> exceeded the maximum allowed limit (1000).</p>
                            <p><strong>Resolution:</strong> Ensure the <code>query</code> parameter contains text and <code>limit</code> is between 1 and 1000.</p>
                        </div>

                        <div class="error-card" id="missing-parameter">
                            <div class="error-header">
                                <span class="error-title"><code>missing-parameter</code> - Missing Required Parameter</span>
                                <span class="badge badge-400">HTTP 400</span>
                            </div>
                            <p><strong>Cause:</strong> A required HTTP request parameter (e.g. <code>query</code>) was not supplied in the request.</p>
                            <p><strong>Resolution:</strong> Include <code>?query=...</code> in your request URL.</p>
                        </div>

                        <div class="error-card" id="type-mismatch">
                            <div class="error-header">
                                <span class="error-title"><code>type-mismatch</code> - Parameter Type Mismatch</span>
                                <span class="badge badge-400">HTTP 400</span>
                            </div>
                            <p><strong>Cause:</strong> A parameter was supplied with an incompatible data type (for example passing non-numeric text like <code>limit=abc</code>).</p>
                            <p><strong>Resolution:</strong> Pass integer values for numeric parameters like <code>limit</code> and <code>pageSize</code>.</p>
                        </div>

                        <div class="error-card" id="upstream-error">
                            <div class="error-header">
                                <span class="error-title"><code>upstream-error</code> - Upstream Service Error</span>
                                <span class="badge badge-502">HTTP 502 / 504</span>
                            </div>
                            <p><strong>Cause:</strong> The external Europe PMC Articles API or Grants (Grist) API failed to respond, returned a 5xx error, or timed out.</p>
                            <p><strong>Resolution:</strong> Retry the request after a short interval.</p>
                        </div>

                        <div class="error-card" id="internal-error">
                            <div class="error-header">
                                <span class="error-title"><code>internal-error</code> - Internal Server Error</span>
                                <span class="badge badge-500">HTTP 500</span>
                            </div>
                            <p><strong>Cause:</strong> An unhandled internal application error occurred.</p>
                            <p><strong>Resolution:</strong> Check server logs for stack traces.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }
}
