#!/usr/bin/env bash

curl -v -H "Content-Type: application/json" -X POST http://localhost:8888/term/index -d '{
	"terms": [
	    {
            "term": "मराठी",
            "frequency": 1.0,
            "vector": [1.0, 2.0, 3.0],
            "synonyms":
            {
                "bla1": 0.1,
                "bla2": 0.2
            },
            "antonyms":
            {
                "bla3": 0.1,
                "bla4": 0.2
            },
            "tags": "tag1 tag2",
            "features":
            {
                "POS": "V",
                "GEN": "M"
            }
	    },
	    {
            "term": "term2",
            "frequency": 1.0,
            "vector": [1.0, 2.0, 3.0],
            "synonyms":
            {
                "bla1": 0.1,
                "bla2": 0.2
            },
            "antonyms":
            {
                "bla3": 0.1,
                "bla4": 0.2
            },
            "tags": "tag1 tag2",
            "features":
            {
                "POS": "N",
                "GEN": "F"
            }
	    }
   ]
}'

