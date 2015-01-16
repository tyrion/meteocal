def getQuery(id, name, description, icon):
    return "INSERT INTO `meteocal`.`weather_conditions` (`id`, `name`, `description`, `icon`) VALUES ('%s', '%s', '%s', '%s');" % (id, name, description, icon)

with open("WeatherConditions.txt","r") as f, open("weatherConditionsInsert.sql", "w") as g:
    lines = [(i, x.strip('\n')) for i, x in enumerate(f.readlines())]
    [g.write(getQuery(id, name, description, icon) + '\n') for (i, id), (j, name), (k, description), (w, icon) in zip(*map(lambda y: filter(lambda x: x[0] % 4 == y, lines), [0, 1, 2, 3]))]
