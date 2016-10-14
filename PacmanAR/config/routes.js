var users2 = require('./../server/controllers/users.js');
module.exports = function Routes (app, io) {
	app.get('/', function (req,res) { users2.index(req,res) });
	app.get('/classic', function (req,res) { users2.pacman1(req,res) });
	app.get('/survivor', function (req,res) { users2.pacman2(req,res) });

	// app.get('/users', function (req,res) { users.index(req,res) });
	// app.get('/users/index.json', function (req,res) { users.index_json(req,res) });
	// app.get('/users/new', function (req,res) { users.new(req,res) });
	// app.post('/users', function (req,res) { users.create(req,res) });
	// app.get('/users/:id', function (req,res) { users.show(req,res) });
	// app.get('/users/:id/edit', function (req,res) { users.edit(req,res) });
	// app.post('/users/newUser_json', function (req,res) { users.newUser_json(req,res) });
	
	var users = {};
	var coins = [];
	var count = 0;
	var map = [
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0],
            [0,1,0,0,0,1,0,1,0,1,0,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,1,0,1,0,1,0,0,0,1,0],
            [0,1,0,1,1,1,0,1,0,1,0,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,1,1,1,0,1,0,1,0,1,1,1,0,1,0],
            [0,1,0,1,0,0,0,1,0,1,0,1,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,1,0,1,0,1,0,0,0,1,0,1,0],
            [0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0],
            [0,1,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,1,0],
            [0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,0],
            [0,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,1,0],
            [0,0,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,0,0],
            [0,1,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,0,0,1,0,1,1,0,1,1,1,1,0],
            [0,0,1,0,1,0,1,1,1,1,0,1,1,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,0,0],
            [0,1,1,0,1,0,1,1,1,1,0,1,1,0,1,0,0,0,0,1,0,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0],
            [0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,0],
            [0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0],
            [0,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,0],
            [0,1,0,1,0,0,0,1,0,1,0,1,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,1,0,1,0,1,0,0,0,1,0,1,0],
            [0,1,0,1,1,1,0,1,0,1,0,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,1,1,1,0,1,0,1,0,1,1,1,0,1,0],
            [0,1,0,0,0,1,0,1,0,1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,1,0],
            [0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
        ];

    for (var i=0; i<map.length; i++){
    	for (var j=0; j<map[i].length; j++){
    		if (map[i][j] == 1){
    			var posY = i*30+42;
    			var posX = j*30+12;
    			var id = (posY-12).toString() + '-' + (posX-12).toString();
    			coins.push({id: id, x: posX, y: posY});
    		}
    	}
    }

	io.on('connection', function (socket) {
		socket.on('login', function (data) {
			count++;
			if (count === 1) {
				var info = { x: 30, y: 60, pos: 39, id: socket.id, collision: false, power: false, score: 0 };
			}
			else {
				var info = { x: 1140, y: 60, pos: 37, id: socket.id, collision: false, power: false, score: 0 };
			}
			var user = { name: data.name, info: info };
			socket.emit('current_user', { name: data.name, info: info, map: map, coins: coins });
			socket.emit('other_users', { users: users });
			socket.broadcast.emit('new_user', { user: user, users: users });
			users[socket.id] = user;
			if (count == 2) {
				io.emit('start_game', { users: users });
			}
		})

		socket.on('key_press', function (data) {
			console.log(users[data.info.id].info);
			users[data.info.id].info = data.info;
			socket.broadcast.emit('new_movement', { info: data.info });
		})

		socket.on('eat_coin', function (data) {
			for (var i in coins) {
				if (coins[i].id == data.coin_id) {
					coins.splice(i, 1);
				}
			}
			socket.broadcast.emit('remove_coin', { coin_id: data.coin_id });
			io.emit('update_all_coins', { coins: coins });
		})

		socket.on('update_score', function (data) {
			users[data.id].score = data.score;
			socket.broadcast.emit('broadcast_score', { id: data.id, score: data.score });
		})

		socket.on('disconnect', function (req) {
			delete users[socket.id];
			socket.broadcast.emit('remove_user', { id: socket.id });
			count--;
		})
	});	

};