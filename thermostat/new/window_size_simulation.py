#------------------------------------------------------------------
# window_size_simulation.py
#
# Compares rolling-window sizes for the thermostat's TemperatureBuffer
# using simulated noisy sensor readings. 
#
# Setup: heating mode, set point 72F, true room temperature held at
# 72.5F. Readings follow the thermostat's real schedule (6 reads per
# 10-second cycle, one light decision per cycle).
#------------------------------------------------------------------

import random
from math import floor

class TemperatureBuffer():
    def __init__(self, size=5):
        self.size = size
        self.readings = []

    def addReading(self, value):
        self.readings.append(value)
        if len(self.readings) > self.size:
            self.readings.pop(0)

    def getSmoothedTemp(self):
        if not self.readings:
            return None
        return sum(self.readings) / len(self.readings)

SET_POINT = 72
SENSOR_SD = 0.5
SPIKE_SIZE = 4.0
READ_SECONDS = {0, 1, 2, 3, 4, 9}
DECISION_SECOND = 9
WINDOWS = [1, 3, 5, 7, 10, 15]

def noisy(true_temp, rng, spike_prob):
    value = true_temp + rng.gauss(0, SENSOR_SD)
    if rng.random() < spike_prob:
        value += rng.choice([-SPIKE_SIZE, SPIKE_SIZE])
    return value

def steady_test(window, spike_prob, seed, cycles=20000, true_temp=72.5):
    rng = random.Random(seed)
    buf = TemperatureBuffer(window)
    decisions = []
    for t in range(cycles * 10):
        if (t % 10) in READ_SECONDS:
            buf.addReading(noisy(true_temp, rng, spike_prob))
        if (t % 10) == DECISION_SECOND:
            decisions.append(floor(buf.getSmoothedTemp()) < SET_POINT)
    wrong = sum(decisions) / len(decisions)
    flips = sum(1 for a, b in zip(decisions, decisions[1:]) if a != b) / (len(decisions) - 1)
    return wrong * 100, flips * 100

def lag_test(window, seed, trials=3000, start=70.0, end=74.0):
    rng = random.Random(seed)
    lags = []
    for _ in range(trials):
        buf = TemperatureBuffer(window)
        step_time = 100 + rng.randrange(0, 10)
        t = 0
        while True:
            true_temp = start if t < step_time else end
            if (t % 10) in READ_SECONDS:
                buf.addReading(noisy(true_temp, rng, 0.0))
            if (t % 10) == DECISION_SECOND and t >= step_time:
                if floor(buf.getSmoothedTemp()) >= SET_POINT:
                    lags.append(t - step_time)
                    break
            t += 1
    return sum(lags) / len(lags)

if __name__ == "__main__":
    print("Window | Noise only (wrong%, flip%) | Noise+spikes (wrong%, flip%) | Lag (s)")
    for w in WINDOWS:
        w1, f1 = steady_test(w, 0.0, seed=1)
        w2, f2 = steady_test(w, 0.02, seed=2)
        lag = lag_test(w, seed=3)
        print(f"{w:>6} | {w1:>6.2f} {f1:>6.2f} | {w2:>6.2f} {f2:>6.2f} | {lag:>6.1f}")