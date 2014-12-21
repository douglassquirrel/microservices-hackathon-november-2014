#!/usr/bin/env python
import os
#import webapp2
#import logging
#import time
#from handler import Handler

from flask import Flask, request, session, g, redirect, url_for, abort, render_template, flash
app = Flask(__name__)
app.debug = True

@app.route('/')
def index():
    page_title = "Tic Tac Toe"
    return render_template('index.html', page_title=page_title)

@app.route('/games')
def tictactoe_games():
    page_title = "Tic Tac Toe Games"
    games = [{"turns":[{"X":0,"Y":0},{"X":0,"Y":2}], "status":"nought wins"},]
    players = ["nought", "cross"]
    return render_template('games.html', page_title=page_title,games=games,players=players)

@app.route('/play')
def tictactoe_player():
    page_title = "Let's play Tic Tac Toe"
    player = "Nought"
    games = [{"turns":[{"X":0,"Y":0},{"X":0,"Y":2}], "status":"nought wins"},]
    return render_template('player.html', page_title=page_title,player=player, games=games)
    
if __name__ == '__main__':
    app.run()
