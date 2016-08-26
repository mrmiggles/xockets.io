def onOpen(user):
    print ("(python) onOpen " + user.toString())
    return


def onMessage(msg):
    print("(python) onMessage " + msg.getFrom())
    return


def onClose(user):
    print ("(python) onClose " + user.toString())
    return


def onError(err):
    print (err.getMessage())
    return
