// <!DOCTYPE html>
// <html lang='en'>
// 	<head>
// 		<meta charset='utf-8'>
// 		<title>Pac Man Lite</title>
// 		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
// 		<link rel="stylesheet" type="text/css" href="stylesheets/style.css">
// 	</head>
// 	<body>
// 		<div id='info'>
// 			<h1 class='yellow'>PACMAN</h1>
// 			<h2 class='score'>Score: 0</h2>
// 		</div>
// 		<div id='container'></div>
// 		<div id='coins'></div>
// 		<div id='hero'></div>
// 		<div id='lives'></div>
// 		<div id='enemy1'></div>
// 		<div id='enemy2'></div>
// 		<div id='enemy3'></div>
// 		<div id='enemy4'></div>
// 		<div id='start'><button class='start'>Click to Start</button></div>
// 		<div id='ready'>READY!</div>
// 		<div id='end'></div>
// 	</body>
// </html>

// <script type="text/javascript">
	// Global variables (mainly for setInterval)
	var game = new Game();
	var pacman = new Pacman(270, 510, 37);
	var ghost = [];

	var upPac;
	var upGho;
	var mainTime;

	var pacLoop = function() { 
		pacman.update(game, ghost);
	}

	var ghostLoop = function() {
		for (var i in ghost) {
			ghost[i].update(game, pacman, ghost);
		}
	}

	var timeLoop = function() {
		game.timer();
	}

	var sound = {};
	sound.start = new Audio('sounds/pacman_beginning.wav');
	sound.background1 = new Audio('sounds/pacman_background.wav');
	sound.background2 = new Audio('sounds/pacman_power.wav');
    sound.eat = new Audio('sounds/pacman_chomp.wav');
    sound.power = new Audio('sounds/pacman_eatghost.wav')
    sound.die = new Audio('sounds/pacman_death.wav');
    sound.end = new Audio('sounds/pacman_intermission.wav');
	
	game.drawMap();
	game.drawCoins();
	game.initialize();

	document.getElementById('start').onclick = function() {
		this.innerHTML = '';
		sound.start.play();
		mainTime = setInterval(timeLoop, 1000);
	}

	document.onkeydown = function (e) {
		if (e.keyCode >= 37 && e.keyCode <= 40) {
			pacman.info.pos_temp = e.keyCode;
			pacman.detectCollision(game);
		}
	}

	function Game() {
		this.info = { tile: 10, conv: 3, timeElapsed: 0 };
		this.coins = [];
		this.map = [
				[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
				[0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0],
				[0,1,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,1,0],
				[0,1,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,1,0],
				[0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0],
				[0,1,0,0,1,0,1,0,0,0,0,0,1,0,1,0,0,1,0],
				[0,1,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,1,0],
				[0,0,0,0,1,0,0,0,9,0,9,0,0,0,1,0,0,0,0],
				[9,9,9,0,1,0,9,9,9,9,9,9,9,0,1,0,9,9,9],
				[0,0,0,0,1,0,9,0,0,2,0,0,9,0,1,0,0,0,0],
				[7,9,9,9,1,9,9,0,9,9,9,0,9,9,1,9,9,9,7],
				[0,0,0,0,1,0,9,0,0,0,0,0,9,0,1,0,0,0,0],
				[9,9,9,0,1,0,9,9,9,9,9,9,9,0,1,0,9,9,9],
				[0,0,0,0,1,0,9,0,0,0,0,0,9,0,1,0,0,0,0],
				[0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0],
				[0,1,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,1,0],
				[0,1,1,0,1,1,1,1,1,9,1,1,1,1,1,0,1,1,0],
				[0,0,1,0,1,0,1,0,0,0,0,0,1,0,1,0,1,0,0],
				[0,1,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,1,0],
				[0,1,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,1,0],
				[0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0],
				[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
		];

		this.drawMap = function() {
			for (var i=0; i<this.map.length; i++) {
				for (var j=0; j<this.map[i].length; j++) {
					if (this.map[i][j] === 1 || this.map[i][j] === 7 || this.map[i][j] === 9) {
						$('#container').append("<div class='path'></div>");
					}
					else if (this.map[i][j] === 2) {
						$('#container').append("<div class='door'></div>");
					}
					else if (this.map[i][j] === 0) {
						$('#container').append("<div class='brick'></div>");
					}
				}
			}
		}

		this.drawCoins = function() {
            var id = 1;
            for (var i=0; i<(this.map.length); i++) {
            	for (var j=0; j<(this.map[i].length); j++) {
            		if (this.map[i][j] == 1) {
            			var posY = i*30+42;
            			var posX = j*30+12;
            			$('#coins').append("<img id='" + id + "' class='coin'" +
            				"src='images/coin.jpg' style='top: " + posY + "px; left: " + posX + "px;'>");
            			this.coins.push({id: id, x: posX, y: posY});
            			id++;
            		}
            	}
            }

            // This replaces the coins with power pellets
            $("img[id='23']").replaceWith("<img id='23' class='power'" +
            	"src='images/powerpellet.gif' style='top: 120px; left: 30px;'>");
            $("img[id='28']").replaceWith("<img id='28' class='power'" +
            	"src='images/powerpellet.gif' style='top: 120px; left: 510px;'>");
            $("img[id='102']").replaceWith("<img id='102' class='power'" +
            	"src='images/powerpellet.gif' style='top: 510px; left: 30px;'>");
            $("img[id='115']").replaceWith("<img id='115' class='power'" +
            	"src='images/powerpellet.gif' style='top: 510px; left: 510px;'>");                   
		}

		this.initialize = function() {
			ghost[1] = new Ghost(270, 270, 38, 1);
			ghost[2] = new Ghost(270, 330, 38, 2);
			ghost[3] = new Ghost(240, 330, 39, 3);
			ghost[4] = new Ghost(300, 330, 37, 4);
			pacman.draw();
			pacman.drawLives();
			for (var j in ghost) {
				ghost[j].draw(pacman);
			}
		}

		this.timer = function() {
			this.info.timeElapsed++;
			if (this.info.timeElapsed === 4) {
				upPac = setInterval(pacLoop, 80);
				upGho = setInterval(ghostLoop, 90);
				sound.background1.loop = true;
				sound.background1.play();
				$('#ready').html('');
			}
		}
	}

	function Pacman (x, y, pos) {
		this.info = { x: x, y: y, pos: pos, pos_temp: pos, collision: false, mode: 'scatter', score: 0, lives: 3 };
		var timer = 0;

		this.draw = function () {
			$('#hero').html("<img class='pacman' src='images/pacman" + this.info.pos +
				".gif' style='top: " + this.info.y + "px; left: " + this.info.x + "px;'>");
		}

		this.drawLives = function() {
			$('#lives').html('');
			for (var i=1; i<this.info.lives; i++) {
				var posX = i * 30 - 30;
				$('#lives').append("<img class='pacman' src='images/pacman37.png' style='top: 690px; left: " + posX + "px;'>");
			}
		}

		this.update = function (game, ghost) {
			// Sets proper music for chase & scatter mode
			if (this.info.mode === 'chase' || this.info.mode === 'scatter') {
				sound.background2.pause();
				sound.background1.loop = true;
				sound.background1.play();
			}
			else {
				timer++;
				// Ghosts will start to flash to warn Pacman they will go back to chase mode soon
				if (timer >= 70 && timer < 90) {
					for (var i in ghost) {
						if (ghost[i].info.mode === 'frightened') {
							ghost[i].info.mode = 'chase_warning';
						}
					}
				}
				// Resets ghosts back to chase mode
				else if (timer === 90) {
					this.info.mode = 'chase';
					for (var i in ghost) {
						ghost[i].info.mode = 'chase';
					}
					clearInterval(upGho);
					upGho = setInterval(ghostLoop, 90);
					timer = 0;
				}
			}
    		this.detectCollision(game);
			this.move(game);
			this.draw();
			this.updateLife(game, ghost);
            this.updateCoins(game, ghost);
		}

		this.detectCollision = function (game) {
			var block = game.info.tile * game.info.conv;
            this.info.collision = false;
            // Only checks for collision when Pacman is in center of block (no overlap)
            if (this.info.y % block === 0 && this.info.x % block === 0) {
            	var x = this.info.x / block;
				var y = this.info.y / block;
				var left = game.map[y-1][Math.floor(x - 0.1)];
            	var up = game.map[Math.floor(y - 0.1)-1][x];
            	var right = game.map[y-1][Math.ceil(x + 0.1)];
            	var down = game.map[Math.ceil(y + 0.1)-1][x];
            	var options = [left, up, right, down];
            	// This means user is trying to change directions
	            if (this.info.pos !== this.info.pos_temp) {
	            	// If next block is a path, it will change Pacman's position accordingly
	            	if (options[this.info.pos_temp - 37] === 1 || options[this.info.pos_temp - 37] === 9) {
	            		this.info.pos = this.info.pos_temp;
	            	}
	            }
	            // Checks if next block is a wall or a tunnel
            	if (options[this.info.pos - 37] === 0) {
            		this.info.collision = true;
            	}
            	else if (options[this.info.pos - 37] === undefined) {
            		this.info.collision = undefined;
            	}
            }
		}

		this.move = function (game) {
			var block = game.info.tile * game.info.conv;
			var sign = [-1, -1, 1, 1];
			// Checks if next block is a tunnel
			if (this.info.collision === undefined) {
				if (this.info.pos === 37) {
					this.info.x = game.map[0].length * block - block;
				}
				else if (this.info.pos === 39) {
					this.info.x = 0;
				}
			}
			// Only moves Pacman if next block is a path
			else if (this.info.collision === false){
				if (this.info.pos === 37 || this.info.pos === 39) {
					this.info.x += sign[this.info.pos - 37] * game.info.tile;
				}
				else if (this.info.pos === 38 || this.info.pos === 40) {
					this.info.y += sign[this.info.pos - 37] * game.info.tile;
				}
			}
		}

		this.updateCoins = function (game, ghost) {
			var that = this;
			for (var i in game.coins) {
				var delta = getDistance(this.info.x+15, game.coins[i].x, this.info.y+15, game.coins[i].y);
				// This means Pacman touched a coin or power pellet
				if (delta <= 6) {
					var el = document.getElementById(game.coins[i].id);
					var id = game.coins[i].id;
					// This means Pacman ate a power pellet
					if (id === 23 || id === 28 || id === 102 || id === 115) {
						timer = 0;
						this.info.score += 100;
						sound.background1.pause();
						sound.background2.loop = true;
						sound.background2.play();
						this.info.mode = 'frightened';
						for (var j in ghost) {
							// Turns all ghosts blue to signify frightened mode
							$('#enemy').html("<img class='ghost' src='images/ghost5.png' style='top: " +
								ghost[j].info.y + "px; left: " + ghost[j].info.x + "px;'>");

							ghost[j].info.mode = 'frightened';
							// Forces ghost to reverse direction
							if (ghost[j].info.pos <= 38) {
								ghost[j].info.pos += 2;
							}
							else {
								ghost[j].info.pos -= 2;
							}
							// Ghosts are slightly slower in frightened mode
							clearInterval(upGho);
							upGho = setInterval(ghostLoop, 120);
						}
					}
					// This means Pacman ate a coin
					else {
						this.info.score += 10;
						sound.eat.play();
					}
					game.coins.splice(i, 1);
					document.getElementById('coins').removeChild(el);
					document.getElementById('info').children[1].innerHTML = "Score: " + this.info.score;
				}
			}
			// Pacman wins if he eats all the coins
			if (game.coins.length === 0){
				clearInterval(upGho);
				clearInterval(upPac);
				clearInterval(mainTime);
				$('#end').append("<p id='winner'>YOU WIN!!!</p>");
				sound.background1.pause();
				sound.background2.pause();
				sound.end.play();
			}
		}

		this.updateLife = function (game, ghost) {
			var that = this;
			for (var i in ghost) {
				var delta = getDistance(this.info.x, ghost[i].info.x, this.info.y, ghost[i].info.y);
				// This means Pacman touched a ghost
				if (delta < 15) {
					// This means the ghost ate Pacman
					if (ghost[i].info.mode === 'chase' || ghost[i].info.mode === 'scatter') {
						sound.background1.pause();
						sound.background2.pause();
						sound.die.play();
						clearInterval(upGho);
						clearInterval(upPac);
						clearInterval(mainTime);
						this.info.lives--;
						// Game is over only after Pacman loses all 3 lives
						if (this.info.lives === 0) {
							$('#end').append("<p id='loser'>GAME OVER</p>");
							document.getElementById('hero').remove();
						}
						// Otherwise game resets Pacman and ghosts to starting positions
						else {
							this.info.x = 270;
							this.info.y = 510;
							this.info.pos = 37;
							mainTime = setInterval(timeLoop, 1000);
							game.info.timeElapsed = 2;
							$('#ready').html("READY!");
							game.initialize();
						}
					}
					// This means Pacman ate the ghost and it will return to the ghost house
					else {
						if (ghost[i].info.id === 1 || ghost[i].info.id === 2) {
							ghost[i].info.x = 270;
						}
						else if (ghost[i].info.id === 3) {
							ghost[i].info.x = 240;
						}
						else {
							ghost[i].info.x = 300;
						}
						ghost[i].info.y = 330;
						ghost[i].info.mode = 'freeze';
						ghost[i].info.timer = 0;
						this.info.score += 500;
						sound.power.play();
						document.getElementById('info').children[1].innerHTML = "Score: " + this.info.score;
					}
				}
			}
		}
	}

	function Ghost (x, y, pos, id) {
		this.info = { id: id, x: x, y: y, pos: pos, pos_temp: pos, collision: false, mode: 'scatter' };
		var timer = 0;

		this.draw = function(pacman) {
			if (this.info.mode === 'chase' || this.info.mode === 'scatter' || this.info.mode ==='freeze') {
				$("#enemy" + this.info.id).html("<img class='ghost' src='images/ghost" + this.info.id + ".gif'" +
					"style='top: " + this.info.y + "px; left: " + this.info.x + "px;'>");
			}
			else if (this.info.mode === 'chase_warning') {
				$("#enemy" + this.info.id).html("<img class='ghost' src='images/ghost5.gif'" +
					"style='top: " + this.info.y + "px; left: " + this.info.x + "px;'>");
			}
			else {
				$("#enemy" + this.info.id).html("<img class='ghost' src='images/ghost5.png'" +
					"style='top: " + this.info.y + "px; left: " + this.info.x + "px;'>");
			}
		}

		this.update = function (game, pacman, ghost) {
			this.findPath(game, pacman, ghost);
			// Clyde only comes out of the ghost house after Pacman has eaten 30 coins
			if (this.info.id === 3) {
				if (game.coins.length <= 126) {
					this.move(game);
				}
			}
			// Inky only comes out of the ghost house after Pacman has eaten 56 coins
			else if (this.info.id === 4) {
				if (game.coins.length <= 100) {
					this.move(game);
				}
			}
			else {
				this.move(game);
			}
			this.draw(pacman);
		}

		this.move = function (game) {
			if (this.info.mode !== 'freeze') {
				var block = game.info.tile * game.info.conv;
				var options = [-1, -1, 1, 1];
				// Checks if going through tunnel
				if (this.info.collision === undefined){
					if (this.info.pos === 37){
						this.info.x = game.map[0].length * block - block;
					}
					else if (this.info.pos === 39) {
						this.info.x = 0;
					}
				}
				// Only moves ghost if the next block is a path
				else if (this.info.collision === false) {
					if (this.info.pos === 37 || this.info.pos === 39){
						this.info.x += options[this.info.pos - 37] * game.info.tile;
					}
					else if (this.info.pos === 38 || this.info.pos === 40){
						this.info.y += options[this.info.pos - 37] * game.info.tile;
					}
				}
			}
		}

		this.findPath = function (game, pacman, ghost){
			var block = game.info.tile * game.info.conv;
			var x = this.info.x / block;
			var y = this.info.y / block;
			this.info.collision = false;

            // This prevents errors when in between blocks
            if (this.info.y % block === 0 && this.info.x % block === 0) {
				var left = game.map[y-1][Math.floor(x - 0.1)];
            	var up = game.map[Math.floor(y - 0.1)-1][x];
            	var right = game.map[y-1][Math.ceil(x + 0.1)];
            	var down = game.map[Math.ceil(y + 0.1)-1][x];
            	var options = [left, up, right, down];
            	var sign = [[-1, 0], [0, -1], [1, 0], [0, 1]];
            	var distance = [];
            	this.info.pos_temp = 37;

            	// Calculate distance between ghost and 'target'
            	// Ghosts start off in scatter mode and...
            	if (this.info.mode === 'scatter') {
            		timer++;
            		// After 7 seconds, they change to chase mode
            		// Timer max is 30 instead of 90 because it's only checking a third of the time
            		if (timer === 30) {
            			this.info.mode = 'chase';
            			pacman.info.mode = 'chase';
            			timer = 0;
            		}

            		// Calculate distance between Blinky and top right tile
            		if (this.info.id === 1) {
            			for (var i=0; i<4; i++) {
            				if (options[i] !== 0) {
            					distance[i] = getDistance(this.info.x + block*sign[i][0], game.map[0].length * block,
            											  this.info.y + block*sign[i][1], 0);
            				}
            			}
            		}
            		// Calculate distance between Pinky and top left tile
            		else if (this.info.id === 2) {
            			for (var i=0; i<4; i++) {
            				if (options[i] !== 0) {
            					distance[i] = getDistance(this.info.x + block*sign[i][0], 0,
            											  this.info.y + block*sign[i][1], 0);
            				}
            			}		
            		}
            		// Keeps Clyde and Inky in the ghost house at the start of the game
            		else {
            			this.info.mode = 'freeze';
            			timer = 0;
            		}
            	}
            	// If ghosts are in freeze mode (also after getting eaten), they come out of the ghost house at different times
            	else if (this.info.mode === 'freeze') {
            		timer++;
            		if (this.info.id === 3) {
            			if (timer === 60) {
            				this.info.mode = 'chase';
            				timer = 0;
            			}
            		}
            		else if (this.info.id === 4) {
            			if (timer === 90) {
            				this.info.mode = 'chase';
            				timer = 0;
            			}
            		}
            		else {
            			if (timer === 30) {
            				this.info.mode = 'chase';
            				timer = 0;
            			}
            		}
            	}
            	// This means ghosts are in chase or frightened mode (utilizes same algorithm for target tile)
            	else {
            		// Blinky's (Red) target is Pacman's current tile
            		if (this.info.id === 1) {
            			for (var i=0; i<4; i++) {
            				// Only considers valid options (not bricks)
            				if (options[i] !== 0) {
            					// This prevents ghost from going back into the ghost house
            					if (options[3] !== 2 || i !== 3) {
            						distance[i] = getDistance(this.info.x + block*sign[i][0], pacman.info.x,
            												  this.info.y + block*sign[i][1], pacman.info.y);
            					}
            				}
            			}
            		}
            		// Pinky's target is 4 tiles ahead of Pacman
            		else if (this.info.id === 2) {
            			var temp_x = pacman.info.x;
            			var temp_y = pacman.info.y;
            			if (pacman.info.pos === 37) {
            				temp_x -= 4 * block;
            			}
            			// Shifting 4 tiles to left and 4 tiles up due to overflow error in original game
            			else if (pacman.info.pos === 38) {
            				temp_y -= 4 * block;
            				temp_x -= 4 * block;
            			}
            			else if (pacman.info.pos === 39) {
            				temp_x += 4 * block;
            			}
            			else {
            				temp_y += 4 * block;
            			}
            			for (var i=0; i<4; i++) {
            				if (options[i] !== 0) {
            					// This prevents ghost from going back into the ghost house
            					if (options[3] !== 2 || i !==3 ) {
            						distance[i] = getDistance(this.info.x + block*sign[i][0], temp_x,
            												  this.info.y + block*sign[i][1], temp_y);
            					}
            				}
            			}
            		}
            		// Clyde's (Orange) has two different targets depending on the scenario
            		else if (this.info.id === 3) {
            			var temp_distance = getDistance(this.info.x, pacman.info.x, this.info.y, pacman.info.y);
            			// His target is Pacman's current tile if he's more than 8 squares away from Pacman
        				if (temp_distance > 8 * block) {
        					for (var i=0; i<4; i++) {
        						if (options[i] !== 0) {
        							// This prevents ghost from going back into the ghost house
	    							if (options[3] !== 2 || i !== 3) {
		    							distance[i] = getDistance(this.info.x + block*sign[i][0], pacman.info.x,
		    													  this.info.y + block*sign[i][1], pacman.info.y);
		    						}
        						}
        					}
        				}
        				// Otherwise his target is the lower left corner of the map 
        				else {
        					for (var i=0; i<4; i++) {
        						if (options[i] !== 0) {
        							// This prevents ghost from going back into the ghost house
        							if (options[3] !== 2 || i !== 3) {
	        							distance[i] = getDistance(this.info.x + block*sign[i][0], 0,
	        													  this.info.y + block*sign[i][1], game.map.length * block);
	        						}
        						}
        					}	
        				}
            		}
            		// Inky's (Blue) target is a combination of 2 factors
            		else {
            			// First, select the position 2 tiles ahead of Pacman
            			var temp_x = pacman.info.x;
            			var temp_y = pacman.info.y;
            			if (pacman.info.pos === 37) {
            				temp_x -= 2 * block;
            			}
            			// Shifting 2 tiles to left and 2 tiles up due to overflow error in original game
            			else if (pacman.info.pos === 38) {
            				temp_y -= 2 * block;
            				temp_x -= 2 * block;
            			}
            			else if (pacman.info.pos === 39) {
            				temp_x += 2 * block;
            			}
            			else {
            				temp_y += 2 * block;
            			}

            			// Draw a line from Blinky's current position to this target square and double it
            			// That becomes the actual target square for Inky
            			temp_x = 2 * temp_x - ghost[1].info.x;
            			temp_y = 2 * temp_y - ghost[1].info.y;
            			for (var i=0; i<4; i++) {
            				if (options[i] !== 0) {
            					// This prevents ghost from going back into the ghost house
            					if (options[3] !== 2 || i !== 3) {
	            					distance[i] = getDistance(this.info.x + block*sign[i][0], temp_x,
	            											  this.info.y + block*sign[i][1], temp_y);
	            				}
            				}
            			}
            		}
            	}

            	// Determine best path depending on the mode
            	if (this.info.mode === 'scatter' || this.info.mode === 'chase') {
            		// Customize ghost's movement until he exits the ghost house
            		if (this.info.x === 240 && this.info.y === 330) {
            			this.info.pos_temp = 39;
            		}
            		else if (this.info.x === 270 && this.info.y === 330) {
            			this.info.pos_temp = 38;
            		}
            		else if (this.info.x === 270 && this.info.y === 300) {
            			this.info.pos_temp = 38;
            		}
            		else if (this.info.x === 300 && this.info.y === 330) {
            			this.info.pos_temp = 37;
            		}
            		// Finds shortest path
            		else {
			    		var min = 1000;
						for (var i=0; i<4; i++) {
							if (distance[i] < min) {
								min = distance[i];
								this.info.pos_temp = 37 + i;
							}
						}

						// If shortest path requires backtracking, need to find next shortest path
						if (Math.abs(this.info.pos - this.info.pos_temp) === 2) {
							distance[this.info.pos_temp - 37] = 1000;
							min = 1000;
							for (var i=0; i<4; i++) {
								if (distance[i] < min) {
									min = distance[i];
									this.info.pos_temp = 37 + i;
								}
							}
						}	
            		}
            	}
            	// Finds longest path
            	else if (this.info.mode === 'frightened' || this.info.mode === 'chase_warning') {
            		// Keeps ghost in ghost house if he is in frightened mode
            		if (this.info.x === 240 && this.info.y === 330) {
            			this.info.pos_temp = 39;
            		}
            		else if (this.info.x === 270 && this.info.y === 330) {
            			this.info.pos_temp = 37;
            		}
            		else if (this.info.x === 300 && this.info.y === 330) {
            			this.info.pos_temp = 37;
            		}
            		else {
            			var max = 0;
            			for (var i=0; i<4; i++) {
            				if (distance[i] > max) {
            					max = distance[i];
            					this.info.pos_temp = 37 + i;
            				}
            			}

            			// If longest path requires backtracking, need to find next longest path
            			if (Math.abs(this.info.pos - this.info.pos_temp) === 2) {
            				distance[this.info.pos_temp - 37] = 0;
            				max = 0;
            				for (var i=0; i<4; i++) {
            					if (distance[i] > max) {
            						max = distance[i];
            						this.info.pos_temp = 37 + i;
            					}
            				}
            			}
            		}
            	}
            	// After all the calculations, update ghost's position
            	this.info.pos = this.info.pos_temp;

            	// Checks if next block is a tunnel
            	if (this.info.pos === 37 && left === undefined) {
            		this.info.collision = undefined;
            	}
            	else if (this.info.pos === 39 && right === undefined) {
            		this.info.collision = undefined;
            	}            
            }	           
        }
	}

	function getDistance(x1, x2, y1, y2){
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
// </script>