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
        <p>This is the root of the Entity Suggester's backend REST API.<br/>
        Some examples of possible actions are:<br/></p>
        <ul>
            <li>Suggesting claim properties:</li>
            <ul>
                <li>/entitysuggester/suggest/claimprops/P361,P279,P31</li>
                <li>/entitysuggester/suggest/claimprops/P361,P279&howMany=20</li>
                <li>/entitysuggester/suggest/claimprops/</li>
            </ul>
            <li>Suggesting source ref properties:</li>
            <ul>
                <li>/entitysuggester/suggest/refprops/P143,P41</li>
                <li>/entitysuggester/suggest/refprops/&howMany=30</li>
            </ul>
            <li>Suggesting qualifier properties:</li>
            <ul>
                <li>/entitysuggester/suggest/qualifiers/P31</li>
                <li>/entitysuggester/suggest/qualifiers/P31&howMany=30</li>
            </ul>
            <li>Suggesting values:</li>
            <ul>
                <li>/entitysuggester/suggest/values/P31</li>
                <li>/entitysuggester/suggest/values/P31&howMany=30</li>
            </ul>
            <li>Training the Entity Suggester:</li>
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
