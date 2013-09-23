package org.wikimedia.wikibase.entitysuggester.client;

/**
 * Enumerates the different types of entities that can be suggested.
 *
 * @author Nilesh Chakraborty
 */
public enum EntityType {

    /**
     * Properties used in claims
     */
    CLAIM_PROPERTY,
    /**
     * Properties used in source references
     */
    REF_PROPERTY,
    /**
     * Properties used in qualifiers
     */
    QUALIFIER,
    /**
     * Values for properties used in claims
     */
    VALUE;
}
