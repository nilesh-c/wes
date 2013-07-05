<?php

namespace WesPHPClient;

/**
 * EntitySuggesterService helps you leverage the Entity Suggester REST api
 */
class EntitySuggesterService {

    /**
     * @var MyrrixClient
     */
    protected $client;

    /**
     * @param string $host     The hostname
     * @param int    $port     The port
     * @param string $username The username
     * @param string $password The password
     */
    function __construct($host, $port, $username = null, $password = null) {
        $this->client = MyrrixClient::factory(array(
                    'hostname' => $host,
                    'port' => $port,
                    'username' => $username,
                    'password' => $password,
                ));
    }

    /**
     * Gets a recommendation for an unknown item, infer its recommended properties/values using a preference(list of properties and property-values) array.
     *
     * @param array $properties The known properties of the unknown item
     * @param int   $count       The number of results to retrieve
     *
     * @return array
     */
    public function getRecommendation(array $properties = array(), $type = "property", $count = null) {
        $command = $this->client->getCommand('GetRecommendation', array(
            'properties' => $properties,
            'type'=>$type,
            'howMany' => $count,
                ));

        return $this->client->execute($command)->json();
    }

    /**
     * Sets a list of item-property or item-property+value pairs
     *
     * @param array $properties An array of arrays with keys 'userID', 'itemID' and 'value'
     *
     * @return bool
     */
    public function ingest(array $properties) {
        $command = $this->client->getCommand('Ingest', array(
            'data' => $properties,
                ));

        return $this->client->execute($command)->isSuccessful();
    }

    /**
     * Sets a list of item-property or item-property+value pairs from a CSV file
     *
     * @param string $fileName The path/filename of the csv file with <item>,<property/value>,<strength value> entries
     *
     * @return bool
     */
    public function ingestFile(string $fileName) {
        $f = fopen($fileName, "r");
        $ingestFeed = array();
        if($f === false)
            throw new Exception("Sorry, cannot open file $fileName");
        while(!feof($f)) {
            $prefs = trim(fgets($f));
            if(empty($prefs)) {
                continue;
            } else {
                $prefs = explode(",", $prefs);
            }
            $ingestFeed[] = array(
                                'userID'=>$prefs[0],
                                'itemID'=>$prefs[1],
                                'value'=>$prefs[2]
                                );
        }
        return $this->ingest($ingestFeed);
    }

    /**
     * Asks Myrrix to refresh, may take time.
     *
     * @return bool
     */
    public function refresh() {
        $command = $this->client->getCommand('Refresh');

        return $this->client->execute($command)->isSuccessful();
    }

    /**
     * Asks if Myrrix is ready to answer requests.
     *
     * @return bool
     */
    public function isReady() {
        $command = $this->client->getCommand('Ready');

        return $this->client->execute($command)->isSuccessful();
    }

    /**
     * @return MyrrixClient
     */
    public function getClient() {
        return $this->client;
    }

}
