var mongoose = require('mongoose')
module.exports = {
	index: function(req, res) {
		res.render('users/index');
	},

	pacman1: function(req, res) {
		res.render('users/pacman1');
	},

	pacman2: function(req, res) {
		res.render('users/pacman2');
	}
}