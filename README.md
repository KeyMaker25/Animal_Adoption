test answer for Android Academy Fundamentals Application

fun totalDailySales(orders: List<Order>): Map<DayOfWeek, Int> {
            val map = HashMap<DayOfWeek, Int>()
            for (order in orders){
                val dayOfWeek = order.creationDate.dayOfWeek
                var sum = 0
                for (item in order.orderLines){
                    sum += item.quantity
                }
                if (map[dayOfWeek] == null){
                    map[dayOfWeek] = sum
                }else{
                    val t = map[dayOfWeek]
                    sum += t as Int
                    map[dayOfWeek] = sum
                }
            }
            return map
        }
