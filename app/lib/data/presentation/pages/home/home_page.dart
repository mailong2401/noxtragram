import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Top bar cố định
      appBar: AppBar(
        backgroundColor: Colors.black,
        title: Text('Instagram', style: TextStyle(color: Colors.white),),
        actions: [
          IconButton(
            icon: Icon(Icons.messenger),
            onPressed: () {

            },
          ),
        ],
      ),

      // Phần giữa scrollable
      body: ListView.builder(
        padding: EdgeInsets.all(16),
        itemCount: 30,
        itemBuilder: (context, index) => Card(
          margin: EdgeInsets.symmetric(vertical: 8),
          child: ListTile(
            title: Text('Item ${index + 1}'),
          ),
        ),
      ),

      // Bottom bar cố định
      bottomNavigationBar: Container(
        color: Colors.black,
        padding: EdgeInsets.symmetric(vertical: 12),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            IconButton(
              icon: Icon(Icons.home, color: Colors.white),
              onPressed: () {},
            ),
            IconButton(
              icon: Icon(Icons.add_box_outlined, color: Colors.white),
              onPressed: () {},
            ),
            IconButton(
              icon: Icon(Icons.person, color: Colors.white),
              onPressed: () {},
            ),
          ],
        ),
      ),
    );
  }
}
