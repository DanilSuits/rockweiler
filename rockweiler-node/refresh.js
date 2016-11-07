/**
 * Created by danil on 8/15/16.
 */
util = require('util')

var leagues = ['AL', 'NL']
var positions = ['pitching','batting']
var year = process.env.YEAR || 2016;

var indexen = {}

for (let currentLeauge of leagues) {
    for (let currentPosition of positions) {
        var url = util.format("http://www.baseball-reference.com/leagues/%s/%d-standard-%s.shtml"
                  , currentLeauge
                  , year
                  , currentPosition);
        console.info(url)
    }
}