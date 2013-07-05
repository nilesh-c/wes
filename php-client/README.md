# Wikidata Entity Suggester PHP Client

This is the PHP client for the [Wikidata Entity Suggester](https://github.com/nilesh-c/wikidata-entity-suggester). It uses the Entity Suggester's REST API to push data and get suggestions.

## Installation via Composer

The best way to use the library is via [Composer](http://getcomposer.org/).

After you install composer, run in console:

```
cd your/working/directory
composer require guzzle/guzzle
composer require wes-php-client/wes-php-client
```

Type 'dev-master' for both, when prompted for the version.

OR

You can manually add the library to your dependencies in the composer.json file:

```
{
    "require": {
        "wes-php-client/wes-php-client": "dev-master"
    }
}
```

and install your dependencies:

```
composer install
```

## Usage

``` php
// Always include this file to use the client
require_once("vendor/autoload.php");

// Instanciate the Myrrix/Entity Suggester service
$wes = new EntitySuggesterService('localhost', 8080);

// Push the data in the file /path/data.csv into the Entity Suggester.
// Please check [this page](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/CSV-file-explanation) for info on how the data should be structured in the CSV file.
$wes->ingestFile("/path/data.csv");

// Refresh the index (add newly added data into the model)
$wes->refresh();

// Get value recommendations for a new item
$recommendation = $myrrix->getRecommendation(array(
                                                    "107----4167410",
                                                    "106",
                                                    "107----215627",
                                                    "156"
                                                ), "value"); // returns an array of property-value pairs and strengths (example: [["107----4167410",0.53],["373----Huntsville  Alabama",0.499]])

// Get property recommendations for a new item
$recommendation = $myrrix->getRecommendation(array(
                                                    "107----4167410",
                                                    "106",
                                                    "107----215627",
                                                    "156"
                                                ), "property"); // returns an array of properties and strengths (example: [["25",0.53],["156",0.499]])

// Specify the number of recommendations (optional)
$recommendation = $myrrix->getRecommendation(array(
                                                    "107----4167410",
                                                    "106",
                                                    "107----215627",
                                                    "156"
                                                ), "property", 20); // returns an array of 20 properties with strengths (example: [["25",0.53],["156",0.499]])

```

See [wesTest.php](wesTest.php) for a crude example/demo. It is temporarily deployed [here](http://173.0.50.123/wesTest.php).
