<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Entity Suggester Backend REST API</title>
    </head>
    <body>
        <h1>Entity Suggester Backend REST API.</h1>
        <p>
            This is the root of the Entity Suggester's backend REST API.<br/>
            Note that the howMany parameter is always required.
            Some examples of possible actions are:<br/>
        </p>
        <ul>
            <li>Suggesting claim properties (GET):</li>
            <ul>
                <li>
                    <a href='/entitysuggester/suggest/claimprops/P47,P373,P30?howMany=20'>
                        /entitysuggester/suggest/claimprops/P47,P373,P30?howMany=20
                    </a>
                </li>
                <li>
                    <a href='/entitysuggester/suggest/claimprops/?howMany=10'>
                        /entitysuggester/suggest/claimprops/?howMany=10
                    </a>
                </li>
            </ul>
            <li>Suggesting source ref properties (GET):</li>
            <ul>
                <li>
                    <a href='/entitysuggester/suggest/refprops/P143?howMany=5'>
                        /entitysuggester/suggest/refprops/P143?howMany=5
                    </a>
                </li>
                <li>
                    <a href='/entitysuggester/suggest/refprops/?howMany=10'>
                        /entitysuggester/suggest/refprops/?howMany=10
                    </a>
                </li>
            </ul>
            <li>Suggesting qualifier properties (GET):</li>
            <ul>
                <li>
                    <a href='/entitysuggester/suggest/qualifiers/P31?howMany=30'>
                        /entitysuggester/suggest/qualifiers/P31?howMany=30
                    </a>
                </li>
            </ul>
            <li>Suggesting values (GET):</li>
            <ul>
                <li>
                    <a href='/entitysuggester/suggest/values/P30?howMany=30'>
                        /entitysuggester/suggest/values/P30?howMany=30
                    </a>
                </li>
            </ul>
            <li>Training the Entity Suggester (POST):</li>
            <ul>
                <li>/entitysuggester/ingest/claimprops</li>
                <li>/entitysuggester/ingest/empty-claimprops</li>
                <li>/entitysuggester/ingest/refprops</li>
                <li>/entitysuggester/ingest/empty-refprops</li>
                <li>/entitysuggester/ingest/qualifiers</li>
                <li>/entitysuggester/ingest/values</li>
            </ul>
        </ul>
    </body>
</html>

