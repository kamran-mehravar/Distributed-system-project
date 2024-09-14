-module(database_logic).
-include_lib("stdlib/include/qlc.hrl").
%% API
-export([initDB/0,
        storeAdvertisement/8,
        getAdvertisements/1,
        deleteAdvertisement/1,
        getAllAdvertisements/0,
        storeCar/13,
        getAllCars/0]).
-record(advertisement, {id,title,description,price,region,manufacturer,productionYear,username}).
-record(car, {
  id,             % ObjectId("6051ed390a85c466ff1347f4")
  region,         % "auburn"
  price,          % "35990"
  year,           % "2010"
  manufacturer,   % "chevrolet"
  model,          % "corvette grand sport"
  fuel,           % "gas"
  transmission,   % "other"
  type,           % "other"
  paint_color,    % null (can be represented as 'undefined' in Erlang)
  description,    % "Carvana is the safer way to buy a car..."
  posting_date,   % "2020-12-02T08:11:30-0600"
  phone           % "4057759884"
}).
initDB()->
  mnesia:create_schema([node()]),
  mnesia:start(),
  createTableAdvertisement(),
  createTableCar().

createTableAdvertisement()->
  try
    mnesia:table_info(type,advertisement),
    io:format("Table 'advertisement' already exists. ~n",[])
  catch
    exit:_->
      mnesia:create_table(advertisement,[{attributes, record_info(fields, advertisement)},
        {type,bag},
        {disc_copies,[node()]}]),
      io:format("Table 'advertisement' Created. ~n",[])
  end.

createTableCar() ->
  try
    mnesia:table_info(type,car),
    io:format("Table 'car' already exists. ~n",[])
  catch
    exit:_->
      mnesia:create_table(car,[{attributes, record_info(fields, car)},
        {type,bag},
        {disc_copies,[node()]}]),
      io:format("Table 'car' Created. ~n",[])
  end.

storeAdvertisement(Id, Title, Description, Price, Region, Manufacturer, ProductionYear, Username) ->
  io:format("Creating Advertisement. ~n",[]),
  AF = fun() ->
    mnesia:write(#advertisement{
      id = Id,
      title = Title,
      description = Description,
      price = Price,
      region = Region,
      manufacturer = Manufacturer,
      productionYear = ProductionYear,
      username = Username
    })
       end,
  mnesia:transaction(AF).

getAdvertisements(Id) ->
  AF = fun()->
    Query = qlc:q([X || X <- mnesia:table(advertisement),
      X#advertisement.id =:= Id]),
    Results = qlc:e(Query),
    lists:map(fun(Item)-> {Item#advertisement.id,
                          Item#advertisement.title,
                          Item#advertisement.description,
                          Item#advertisement.price,
                          Item#advertisement.region,
                          Item#advertisement.manufacturer,
                          Item#advertisement.productionYear,
                          Item#advertisement.username} end, Results)
       end,
  {atomic,Advertisements} = mnesia:transaction(AF),
  Advertisements.

getAllAdvertisements() ->
  AF = fun()->
    Query = qlc:q([X || X <- mnesia:table(advertisement)]),
    Results = qlc:e(Query),
    lists:map(fun(Item)-> {Item#advertisement.id,
      Item#advertisement.title,
      Item#advertisement.description,
      Item#advertisement.price,
      Item#advertisement.region,
      Item#advertisement.manufacturer,
      Item#advertisement.productionYear,
      Item#advertisement.username} end, Results)
       end,
  {atomic,Advertisements} = mnesia:transaction(AF),
  Advertisements.

deleteAdvertisement(Id) ->
  AF = fun()->
    Query = qlc:q([X || X <- mnesia:table(advertisement),
      X#advertisement.id =:= Id]),
    Results = qlc:e(Query),

    F = fun() ->
      lists:foreach(fun(Result) ->
        mnesia:delete_object(Result)
                    end, Results)
        end,
    mnesia:transaction(F)

       end,
  mnesia:transaction(AF).

storeCar(Id, Region, Price, Year, Manufacturer, Model, Fuel, Transmission, Type,
    PaintColor, Description, PostingDate, Phone) ->
  AF = fun() ->
    mnesia:write(#car{
      id=Id,
      region=Region,
      price=Price,
      year=Year,
      manufacturer=Manufacturer,
      model=Model,
      fuel=Fuel,
      transmission=Transmission,
      type=Type,
      paint_color=PaintColor,
      description=Description,
      posting_date=PostingDate,
      phone=Phone
    })
       end,
  mnesia:transaction(AF).

getAllCars() ->
  AF = fun() ->
    Query = qlc:q([X || X <- mnesia:table(car)]),
    Results = qlc:e(Query),
    lists:map(fun(Item) -> {Item#car.id,
      Item#car.region,
      Item#car.price,
      Item#car.year,
      Item#car.manufacturer,
      Item#car.model,
      Item#car.fuel,
      Item#car.transmission,
      Item#car.type,
      Item#car.paint_color,
      Item#car.description,
      Item#car.posting_date,
      Item#car.phone} end, Results)
       end,
  {atomic, Cars} = mnesia:transaction(AF),
  Cars.